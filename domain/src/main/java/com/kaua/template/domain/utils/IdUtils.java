package com.kaua.template.domain.utils;

import java.util.UUID;

public final class IdUtils {

    private IdUtils() {}

    private static UUID generate() {
        return UUID.randomUUID();
    }
    
    public static String generateIdWithHyphen() {
        return generate().toString();
    }

    public static String generateIdWithoutHyphen() {
        return generate().toString().replace("-", "");
    }
}
