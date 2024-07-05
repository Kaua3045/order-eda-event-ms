package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.ProductGateway;
import com.kaua.order.domain.Fixture;
import com.kaua.order.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class ProductServiceGatewayTest {

    @Autowired
    protected ProductGateway productGateway;

    @Test
    void givenAValidSkus_whenCallGetProductsDetails_thenShouldReturnProducts() {
        final var sku = Fixture.itemSku();

        final var products = this.productGateway.getProductsDetailsBySkus(List.of(sku));

        Assertions.assertNotNull(products);
        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals(sku, products.get(0).sku());
    }
}
