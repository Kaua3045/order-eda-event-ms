package com.kaua.order.infrastructure;

import com.kaua.order.config.JpaCleanUpExtension;
import com.kaua.order.infrastructure.transaction.TransactionManagerImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@ComponentScan(
        basePackages = "com.kaua.order",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*RepositoryImpl")
        }
)
@DataJpaTest
@ExtendWith(JpaCleanUpExtension.class)
@Import(TransactionManagerImpl.class)
@Tag("integrationTest")
public @interface DatabaseRepositoryTest {
}
