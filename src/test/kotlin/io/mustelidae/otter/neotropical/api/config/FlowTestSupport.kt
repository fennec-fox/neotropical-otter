package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.NeotropicalOtterApplication
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("embedded")
@ExtendWith(SpringExtension::class)
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class)
@SpringBootTest(classes = [NeotropicalOtterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [DefaultEmbeddedMongo::class, DefaultEmbeddedRedis::class])
@AutoConfigureMockMvc
class FlowTestSupport {

    @Autowired
    final lateinit var mockMvc: MockMvc
}
