package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.CouponGateway;
import org.springframework.stereotype.Component;

@Component
public class CouponServiceGateway implements CouponGateway {

    // TODO: Implementar chamada ao serviço de cupons

    @Override
    public CouponDetails applyCoupon(String couponCode) {
        return new CouponDetails(
                couponCode,
                10f,
                true
        );
    }
}
