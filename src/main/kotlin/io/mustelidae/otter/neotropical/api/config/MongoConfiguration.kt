package io.mustelidae.otter.neotropical.api.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.MongoConfigurationSupport

@Configuration
class MongoConfiguration : MongoConfigurationSupport() {

    @Autowired
    lateinit var mongoProperties: MongoProperties

    override fun getDatabaseName(): String {
        return mongoProperties.mongoClientDatabase
    }

    override fun getInitialEntitySet(): MutableSet<Class<*>> {
        return super.getInitialEntitySet()
    }
}
