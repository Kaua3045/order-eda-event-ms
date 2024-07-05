package com.kaua.order.domain.utils;

import com.kaua.order.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdUtilsTest extends UnitTest {

    @Test
    void testCallIdUtilsGenerateIdWithHyphen() {
        final var id = IdUtils.generateIdWithHyphen();

        Assertions.assertNotNull(id);
        Assertions.assertTrue(
                id.matches("[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}"));
    }

    @Test
    void testCallIdUtilsGenerateIdWithoutHyphen() {
        final var id = IdUtils.generateIdWithoutHyphen();

        Assertions.assertNotNull(id);
        Assertions.assertTrue(id.matches("[a-z0-9]{32}"));
    }
}
