package com.kaua.order.infrastructure.gateways;

import com.kaua.order.application.gateways.ProductGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ProductServiceGateway implements ProductGateway {

    // TODO: Implementar chamada ao serviço de produtos

    @Override
    public List<ProductDetails> getProductsDetailsBySkus(List<String> skus) {
        final var aRandom = new Random();
        final var aProducts = new ArrayList<ProductDetails>();
        skus.forEach(it -> {
            final var aProduct = new ProductDetails(
                    it,
                    BigDecimal.valueOf(aRandom.nextDouble())
            );
            aProducts.add(aProduct);
        });
        return aProducts;
    }
}
