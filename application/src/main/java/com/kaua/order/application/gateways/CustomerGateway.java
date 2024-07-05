package com.kaua.order.application.gateways;

public interface CustomerGateway {

    CustomerDetails getCustomerDetails(String customerId);

    record CustomerDetails(
            String customerId,
            String name,
            String email,
            CustomerAddress address
    ) {}

    record CustomerAddress(
            String street,
            String number,
            String complement,
            String city,
            String state,
            String zipCode
    ) {}
}
