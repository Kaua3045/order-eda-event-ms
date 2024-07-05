package com.kaua.order.config;

import com.kaua.order.infrastructure.events.persistence.EventsJpaRepository;
import com.kaua.order.infrastructure.outbox.OutboxJpaRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class JpaCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(EventsJpaRepository.class),
                appContext.getBean(OutboxJpaRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
