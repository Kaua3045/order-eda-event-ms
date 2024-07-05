package com.kaua.order.application.gateways;

import java.math.BigDecimal;
import java.util.List;

public interface ProductGateway {

    List<ProductDetails> getProductsDetailsBySkus(List<String> skus);

    record ProductDetails(
            String sku,
            BigDecimal unitPrice
    ) {}
}
