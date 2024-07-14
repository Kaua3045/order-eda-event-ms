package com.kaua.order.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.order.OrderStatus;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.infrastructure.ControllerTest;
import com.kaua.order.infrastructure.models.EventRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@ControllerTest(controllers = EventAPI.class)
public class EventAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void givenAValidEventRequestWithShippingCostCalculatedEvent_whenCallCreateEvent_thenShouldReturnCreated() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        final var anEventRequest = new EventRequest(
                "ShippingCostCalculatedEvent",
                "shipping-cost-calculated",
                aPayload
        );

        CompletableFuture<SendResult<String, Object>> successFuture = new CompletableFuture<>();
        successFuture.complete(Mockito.mock(SendResult.class));

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(successFuture);

        final var request = MockMvcRequestBuilders.post("/v1/events")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(anEventRequest));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    void givenAValidEventRequestWithShippingCostCalculatedEventButKafkaThrows_whenCallCreateEvent_thenShouldReturnInternalServerError() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        final var anEventRequest = new EventRequest(
                "ShippingCostCalculatedEvent",
                "shipping-cost-calculated",
                aPayload
        );

        CompletableFuture<SendResult<String, Object>> errorFuture = new CompletableFuture<>();
        errorFuture.completeExceptionally(new RuntimeException("Kafka error"));

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(errorFuture);

        final var request = MockMvcRequestBuilders.post("/v1/events")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(anEventRequest));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    void givenAnNullEventType_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                null,
                "shipping-cost-calculated",
                aPayload
        ));
    }

    @Test
    void givenAnEmptyEventType_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                "",
                "shipping-cost-calculated",
                aPayload
        ));
    }

    @Test
    void givenAnNullTopic_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                "ShippingCostCalculatedEvent",
                null,
                aPayload
        ));
    }

    @Test
    void givenAnEmptyTopic_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("street", "Rua dos Bobos");
        aPayload.put("number", "0");
        aPayload.put("complement", "Casa");
        aPayload.put("city", "São Paulo");
        aPayload.put("state", "SP");
        aPayload.put("zipCode", "00000-000");
        aPayload.put("shippingCompany", "Correios");
        aPayload.put("shippingType", "SEDEX");
        aPayload.put("shippingCost", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                "ShippingCostCalculatedEvent",
                "",
                aPayload
        ));
    }

    @Test
    void givenAnNullPayload_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                "ShippingCostCalculatedEvent",
                "shipping-cost-calculated",
                null
        ));
    }

    @Test
    void givenAnEmptyPayload_whenCallCreateEvent_thenShouldThrowDomainException() throws Exception {
        Assertions.assertThrows(DomainException.class, () -> new EventRequest(
                "ShippingCostCalculatedEvent",
                "shipping-cost-calculated",
                new HashMap<>()
        ));
    }

    @Test
    void givenAnInvalidType_whenCallToProducerRecord_thenShouldThrowIllegalStateException() {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());

        final var anEventRequest = new EventRequest(
                "invalid",
                "shipping-cost-calculated",
                aPayload
        );

        Assertions.assertThrows(IllegalStateException.class,
                anEventRequest::toProducerRecord);
    }

    @Test
    void givenAValidEventRequestWithPaymentTaxCalculatedEvent_whenCallCreateEvent_thenShouldReturnCreated() throws Exception {
        final var aPayload = new HashMap<String, Object>();
        aPayload.put("orderId", IdUtils.generateIdWithoutHyphen());
        aPayload.put("orderStatus", OrderStatus.CREATION_INITIATED.name());
        aPayload.put("totalAmount", BigDecimal.TEN);
        aPayload.put("paymentMethodId", "1");
        aPayload.put("installments", 1);
        aPayload.put("paymentTax", BigDecimal.TEN);
        aPayload.put("aggregateVersion", 1L);
        aPayload.put("who", "test");

        final var anEventRequest = new EventRequest(
                "PaymentTaxCalculatedEvent",
                "payment-tax-calculated",
                aPayload
        );

        CompletableFuture<SendResult<String, Object>> successFuture = new CompletableFuture<>();
        successFuture.complete(Mockito.mock(SendResult.class));

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(successFuture);

        final var request = MockMvcRequestBuilders.post("/v1/events")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(anEventRequest));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.any(ProducerRecord.class));
    }
}
