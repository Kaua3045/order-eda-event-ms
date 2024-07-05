package com.kaua.order.infrastructure.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record OrderCreateRequest(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("items") Set<OrderItemRequest> items,
        @JsonProperty("coupon_code") String couponCode,
        @JsonProperty("payment_method_id") String paymentMethodId,
        @JsonProperty("installments") int installments,
        @JsonProperty("shipping_company") String shippingCompany,
        @JsonProperty("shipping_type") String shippingType
) {
}
