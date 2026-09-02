package com.hospital.resource.common.util;

import java.util.UUID;

public final class UUIDGenerator {

    private UUIDGenerator() {}

    public static UUID generate() {
        return UUID.randomUUID();
    }
}
