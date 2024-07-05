package com.kaua.order.infrastructure.api;

import com.kaua.order.application.repositories.EventStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/faker-controller")
@RestController
public class FakerControllerTest {

    private final EventStore eventStore;

    public FakerControllerTest(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @PostMapping
    public void create() {
        this.eventStore.save(null);
    }
}
