package com.kaua.order.application.gateways;

public interface CouponGateway {

    CouponDetails applyCoupon(String couponCode);

    record CouponDetails(
            String couponCode,
            float percentage,
            boolean isValid
    ) {}
}
