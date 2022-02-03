package io.mustelidae.otter.neotropical.api.domain.booking

import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

/**
 * 검색 조건
 */
interface SearchCondition<T> {

    fun isValid(): Boolean

    fun toSpecs(): Specification<T>

    fun isValidDate(start: LocalDate?, end: LocalDate?): Boolean {
        return ((start != null && end != null) || (start == null && end == null))
    }
}
