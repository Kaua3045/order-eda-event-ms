package com.kaua.order.application.handlers.create;

import com.kaua.order.application.CommandHandlerTest;
import com.kaua.order.application.gateways.CouponGateway;
import com.kaua.order.application.gateways.CustomerGateway;
import com.kaua.order.application.gateways.ProductGateway;
import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.commands.CreateOrderItemCommand;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AsyncCreateOrderHandlerTest extends CommandHandlerTest {

    @Mock
    private CouponGateway couponGateway;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private EventStore eventStore;

    @InjectMocks
    private AsyncCreateOrderHandler asyncCreateOrderHandler;

    @Test
    void givenAValidValuesWithCoupon_whenCallAsyncCreateHandle_thenShouldCreateOrderWithCouponAndSaveEvent() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final var aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aItemUnitPrice = Fixture.itemUnitPrice();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aCouponPercentage = Fixture.couponPercentage();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;
        final var aAddress = Fixture.address("Apt 123");

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );
        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                Set.of(aCommandItems),
                aCouponCode,
                aPaymentMethodId,
                aInstallments,
                aShippingCompany,
                aShippingType,
                aCustomerId,
                aTraceId
        );

        when(couponGateway.applyCoupon(aCouponCode)).thenReturn(new CouponGateway.CouponDetails(
                aCouponCode,
                aCouponPercentage,
                true
        ));
        when(customerGateway.getCustomerDetails(aCustomerId))
                .thenReturn(new CustomerGateway.CustomerDetails(
                        aCustomerId,
                        "John Doe",
                        "john.doe@teste.com",
                        new CustomerGateway.CustomerAddress(
                                aAddress.getStreet(),
                                aAddress.getNumber(),
                                aAddress.getComplement().orElse(null),
                                aAddress.getCity(),
                                aAddress.getState(),
                                aAddress.getZipCode()
                        )
                ));
        when(productGateway.getProductsDetailsBySkus(List.of(aItemSku)))
                .thenReturn(List.of(new ProductGateway.ProductDetails(
                        aItemSku,
                        aItemUnitPrice
                )));
        doNothing().when(eventStore).save(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.asyncCreateOrderHandler.handle(aCommand));

        Mockito.verify(couponGateway, Mockito.times(1)).applyCoupon(aCouponCode);
        Mockito.verify(customerGateway, Mockito.times(1)).getCustomerDetails(aCustomerId);
        Mockito.verify(productGateway, Mockito.times(1)).getProductsDetailsBySkus(List.of(aItemSku));
        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAValidValuesWithoutCoupon_whenCallAsyncCreateHandle_thenShouldCreateOrderWithoutCouponAndSaveEvent() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final var aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aItemUnitPrice = Fixture.itemUnitPrice();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;
        final var aAddress = Fixture.address("Apt 123");

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );
        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                Set.of(aCommandItems),
                null,
                aPaymentMethodId,
                aInstallments,
                aShippingCompany,
                aShippingType,
                aCustomerId,
                aTraceId
        );

        when(customerGateway.getCustomerDetails(aCustomerId))
                .thenReturn(new CustomerGateway.CustomerDetails(
                        aCustomerId,
                        "John Doe",
                        "john.doe@teste.com",
                        new CustomerGateway.CustomerAddress(
                                aAddress.getStreet(),
                                aAddress.getNumber(),
                                aAddress.getComplement().orElse(null),
                                aAddress.getCity(),
                                aAddress.getState(),
                                aAddress.getZipCode()
                        )
                ));
        when(productGateway.getProductsDetailsBySkus(List.of(aItemSku)))
                .thenReturn(List.of(new ProductGateway.ProductDetails(
                        aItemSku,
                        aItemUnitPrice
                )));
        doNothing().when(eventStore).save(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.asyncCreateOrderHandler.handle(aCommand));

        Mockito.verify(couponGateway, Mockito.times(0)).applyCoupon(Mockito.anyString());
        Mockito.verify(customerGateway, Mockito.times(1)).getCustomerDetails(aCustomerId);
        Mockito.verify(productGateway, Mockito.times(1)).getProductsDetailsBySkus(List.of(aItemSku));
        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidEmptyItems_whenCallAsyncCreateHandle_thenShouldThrowException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final var aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;
        final var aAddress = Fixture.address("Apt 123");

        final var expectedErrorMessage = "Product details not found";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                Set.of(aCommandItems),
                null,
                aPaymentMethodId,
                aInstallments,
                aShippingCompany,
                aShippingType,
                aCustomerId,
                aTraceId
        );

        when(customerGateway.getCustomerDetails(aCustomerId))
                .thenReturn(new CustomerGateway.CustomerDetails(
                        aCustomerId,
                        "John Doe",
                        "john.doe@teste.com",
                        new CustomerGateway.CustomerAddress(
                                aAddress.getStreet(),
                                aAddress.getNumber(),
                                aAddress.getComplement().orElse(null),
                                aAddress.getCity(),
                                aAddress.getState(),
                                aAddress.getZipCode()
                        )
                ));
        when(productGateway.getProductsDetailsBySkus(Mockito.anyList()))
                .thenReturn(List.of());

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.asyncCreateOrderHandler.handle(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(0)).applyCoupon(Mockito.anyString());
        Mockito.verify(customerGateway, Mockito.times(1)).getCustomerDetails(aCustomerId);
        Mockito.verify(productGateway, Mockito.times(1)).getProductsDetailsBySkus(Mockito.anyList());
        Mockito.verify(eventStore, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidItemsNotFound_whenCallAsyncCreateHandle_thenShouldThrowException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final var aCustomerId = Fixture.customerId();
        final var aItemSkuOne = Fixture.itemSku();
        final var aItemSkuTwo = Fixture.itemSku();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;
        final var aAddress = Fixture.address("Apt 123");

        final var expectedErrorMessage = "Product details not found";

        final var aCommandItems = Set.of(
                CreateOrderItemCommand.with(
                        aItemSkuOne,
                        Fixture.itemQuantity()
                ),
                CreateOrderItemCommand.with(
                        aItemSkuTwo,
                        Fixture.itemQuantity()
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCommandItems,
                null,
                aPaymentMethodId,
                aInstallments,
                aShippingCompany,
                aShippingType,
                aCustomerId,
                aTraceId
        );

        when(customerGateway.getCustomerDetails(aCustomerId))
                .thenReturn(new CustomerGateway.CustomerDetails(
                        aCustomerId,
                        "John Doe",
                        "john.doe@teste.com",
                        new CustomerGateway.CustomerAddress(
                                aAddress.getStreet(),
                                aAddress.getNumber(),
                                aAddress.getComplement().orElse(null),
                                aAddress.getCity(),
                                aAddress.getState(),
                                aAddress.getZipCode()
                        )
                ));
        when(productGateway.getProductsDetailsBySkus(Mockito.anyList()))
                .thenReturn(List.of(new ProductGateway.ProductDetails(
                        aItemSkuOne,
                        Fixture.itemUnitPrice()
                )));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.asyncCreateOrderHandler.handle(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(0)).applyCoupon(Mockito.anyString());
        Mockito.verify(customerGateway, Mockito.times(1)).getCustomerDetails(aCustomerId);
        Mockito.verify(productGateway, Mockito.times(1)).getProductsDetailsBySkus(Mockito.anyList());
        Mockito.verify(eventStore, Mockito.times(0)).save(Mockito.any());
    }
}
