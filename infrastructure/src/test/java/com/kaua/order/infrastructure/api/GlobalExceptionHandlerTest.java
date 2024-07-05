package com.kaua.order.infrastructure.api;

import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.infrastructure.ControllerTest;
import com.kaua.order.infrastructure.exceptions.EventStoreException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;

@ControllerTest(controllers = FakerControllerTest.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventStore eventStore;

    @Test
    void testThrowDomainException() throws Exception {
        final var expectedErrorMessage = "'items' should not be empty";

        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(eventStore).save(Mockito.any());

        final var request = MockMvcRequestBuilders.post("/v1/faker-controller")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo("DomainException")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));


        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testThrowEventStoreException() throws Exception {
        Mockito.doThrow(EventStoreException.with("java.lang.RuntimeException: Error"))
                .when(eventStore).save(Mockito.any());

        final var request = MockMvcRequestBuilders.post("/v1/faker-controller")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo("Internal server error")));

        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testThrowUnknownException() throws Exception {
        Mockito.doThrow(new RuntimeException("Error"))
                .when(eventStore).save(Mockito.any());

        final var request = MockMvcRequestBuilders.post("/v1/faker-controller")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo("Internal server error")));

        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }
}
