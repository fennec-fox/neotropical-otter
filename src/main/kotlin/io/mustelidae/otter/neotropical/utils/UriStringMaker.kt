package io.mustelidae.otter.neotropical.utils

object UriStringMaker {
    private val regex = "\\{(.*?)}".toRegex()

    fun replace(host: String, text: String, params: Map<String, Any>): String {
        var converted = text

        val findAll = regex.findAll(text)
        findAll.iterator().forEach { result ->
            val target = result.value
            val param = params[result.groupValues[1]] ?: ""
            converted = converted.replace(target, param.toString())
        }

        return host + converted
    }
}
