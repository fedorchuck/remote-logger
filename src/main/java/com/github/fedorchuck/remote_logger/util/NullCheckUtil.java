package com.github.fedorchuck.remote_logger.util;

/**
 * Used for string nullability checks and typesafe number parsing
 *
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class NullCheckUtil {
    public static <T> T anyNotNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static String anyNotNullOrEmpty(String... values) {
        for (String value : values) {
            if (!isNullOrEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    public static Integer tryParseInteger(String str) {
        try {
            if (str == null) {
                return null;
            }
            return Integer.valueOf(str);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static int tryParseInteger(String str, int defaultValue) {
        Integer value = tryParseInteger(str);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns true if the given string is null or is the empty string.
     * <p>Consider normalizing your string references with nullToEmpty.
     *
     * @param string - a string reference to check
     * @return true if the string is null or is the empty string
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
