package com.hospital.resource.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class NumberGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private NumberGenerator() {}

    public static String generatePatientNumber() {
        String datePart = LocalDate.now().format(DATE_FMT);
        int seq = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "PT-" + datePart + "-" + seq;
    }

    public static String generateAdmissionNumber() {
        String datePart = LocalDate.now().format(DATE_FMT);
        int seq = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ADM-" + datePart + "-" + seq;
    }
}
