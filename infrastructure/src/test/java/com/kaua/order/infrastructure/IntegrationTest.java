package com.kaua.order.infrastructure;

import com.kaua.order.config.JpaCleanUpExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@SpringBootTest(classes = {Main.class})
@ExtendWith(JpaCleanUpExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EnableJpaRepositories(basePackages = "com.kaua.order.infrastructure")
@Tag("integrationTest")
public @interface IntegrationTest {
}
