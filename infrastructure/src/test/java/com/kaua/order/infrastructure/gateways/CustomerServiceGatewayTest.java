package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.CustomerGateway;
import com.kaua.order.domain.Fixture;
import com.kaua.order.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CustomerServiceGatewayTest {

    @Autowired
    protected CustomerGateway customerGateway;

    @Test
    void givenAValidCustomerId_whenCallGetCustomerDetails_thenShouldReturnCustomer() {
        final var customerId = Fixture.customerId();

        final var customer = this.customerGateway.getCustomerDetails(customerId);

        Assertions.assertNotNull(customer);
        Assertions.assertEquals(customerId, customer.customerId());
        Assertions.assertEquals("John Doe", customer.name());
        Assertions.assertEquals("john.doe@random.com", customer.email());
        Assertions.assertEquals("rua sem nome", customer.address().street());
        Assertions.assertEquals("123", customer.address().number());
        Assertions.assertEquals("apto 123", customer.address().complement());
        Assertions.assertEquals("city sem nome", customer.address().city());
        Assertions.assertEquals("estado sem nome", customer.address().state());
        Assertions.assertEquals("94850300", customer.address().zipCode());
    }
}
