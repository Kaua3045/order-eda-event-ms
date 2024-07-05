package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.CouponGateway;
import com.kaua.order.domain.Fixture;
import com.kaua.order.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CouponServiceGatewayTest {

    @Autowired
    protected CouponGateway couponGateway;

    @Test
    void givenAValidCouponCode_whenCallApplyCoupon_thenShouldReturnCoupon() {
        final var aCoupon = Fixture.couponCode();

        final var coupon = this.couponGateway.applyCoupon(aCoupon);

        Assertions.assertNotNull(coupon);
        Assertions.assertEquals(aCoupon, coupon.couponCode());
        Assertions.assertEquals(10f, coupon.percentage());
        Assertions.assertTrue(coupon.isValid());
    }
}
