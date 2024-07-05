package com.kaua.order.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.order.domain.Fixture;
import com.kaua.order.infrastructure.ControllerTest;
import com.kaua.order.infrastructure.commands.CommandBus;
import com.kaua.order.infrastructure.models.OrderCreateRequest;
import com.kaua.order.infrastructure.models.OrderItemRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

@ControllerTest(controllers = OrderAPI.class)
public class OrderAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommandBus commandBus;

    @Test
    void givenAValidOrderCreateRequestWithCoupon_whenCallCreateOrder_thenShouldReturnCreated() throws Exception {
        final var aCustomerId = Fixture.customerId();
        final var aItems = OrderItemRequest.with(Fixture.itemSku(), Fixture.itemQuantity());
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();

        final var aOrderCreateRequest = new OrderCreateRequest(
                aCustomerId,
                Set.of(aItems),
                aCouponCode,
                aPaymentMethodId,
                aInstallments,
                aShippingCompany,
                aShippingType
        );

        Mockito.doNothing().when(commandBus).dispatch(Mockito.any(), Mockito.any());

        final var request = MockMvcRequestBuilders.post("/v1/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aOrderCreateRequest));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(commandBus, Mockito.times(1)).dispatch(Mockito.any(), Mockito.any());
    }
}
