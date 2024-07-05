package com.kaua.order.domain;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.Instant;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {}

    public static OrderAddress address(final String complement) {
        final var aAddress = faker.address();
        return OrderAddress.create(
                aAddress.streetName(),
                aAddress.streetAddressNumber(),
                complement,
                aAddress.city(),
                aAddress.state(),
                aAddress.zipCode()
        );
    }

    public static String customerId() {
        return IdUtils.generateIdWithoutHyphen();
    }

    public static String itemSku() {
        return faker.options()
                .option(
                        "t-shirt-red-%s".formatted(faker.number().numberBetween(1, 10)),
                        "t-shirt-blue-%s".formatted(faker.number().numberBetween(1, 10)),
                        "shorts-black-%s".formatted(faker.number().numberBetween(1, 10)),
                        "shorts-white-%s".formatted(faker.number().numberBetween(1, 10)),
                        "shoes-sneakers-%s".formatted(faker.number().numberBetween(1, 10)),
                        "shoes-sandals-%s".formatted(faker.number().numberBetween(1, 10))
                );
    }

    public static int itemQuantity() {
        return faker.number().numberBetween(1, 100);
    }

    public static BigDecimal itemUnitPrice() {
        return BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100));
    }

    public static String shippingCompany() {
        return faker.options()
                .option(
                        "Correios",
                        "Fedex",
                        "DHL",
                        "UPS",
                        "Jamef"
                );
    }

    public static String shippingType() {
        return faker.options()
                .option(
                        "Express",
                        "Normal",
                        "Economic",
                        "Fast",
                        "Super Fast"
                );
    }

    public static String couponCode() {
        return faker.options()
                .option(
                        "BLACKFRIDAY",
                        "CYBERMONDAY",
                        "WELCOME10",
                        "WELCOME20",
                        "WELCOME30"
                );
    }

    public static float couponPercentage() {
        return (float) faker.number().randomDouble(2, 1, 100);
    }

    public static DomainEvent sampleEntityEvent(final String id, final long version) {
        return new SampleEntityEvent(id, version);
    }

    private record SampleEntityEvent(
            String aggregateId, String eventId, String eventType, Instant occurredOn,
            long aggregateVersion, String who, String traceId) implements DomainEvent {

        public SampleEntityEvent(final String id, final long aggregateVersion) {
            this(id, IdUtils.generateIdWithoutHyphen(),
                    "SampleEntityEvent",
                    InstantUtils.now(),
                    aggregateVersion,
                    "user teste",
                    IdUtils.generateIdWithoutHyphen());
        }
    }
}
