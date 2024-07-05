package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.CustomerGateway;
import org.springframework.stereotype.Component;

@Component
public class CustomerServiceGateway implements CustomerGateway {

    //TODO: Implementar chamada ao serviço de cliente

    @Override
    public CustomerDetails getCustomerDetails(String customerId) {
        return new CustomerDetails(
                customerId,
                "John Doe",
                "john.doe@random.com",
                new CustomerAddress(
                        "rua sem nome",
                        "123",
                        "apto 123",
                        "city sem nome",
                        "estado sem nome",
                        "94850300"
                )
        );
    }
}
