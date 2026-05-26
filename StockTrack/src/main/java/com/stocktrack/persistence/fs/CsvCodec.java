package com.stocktrack.persistence.fs;

import java.util.Arrays;

final class CsvCodec {
    private CsvCodec() {
    }

    static String join(String... values) {
        return String.join(",", Arrays.stream(values)
                .map(CsvCodec::escape)
                .toArray(String[]::new));
    }

    static String[] split(String line) {
        return Arrays.stream(line.split(",", -1))
                .map(CsvCodec::unescape)
                .toArray(String[]::new);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("%", "%25")
                .replace(",", "%2C")
                .replace("\n", "%0A")
                .replace("\r", "%0D");
    }

    private static String unescape(String value) {
        return value.replace("%0D", "\r")
                .replace("%0A", "\n")
                .replace("%2C", ",")
                .replace("%25", "%");
    }
}
