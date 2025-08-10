package com.expensetracker.util;

public class EnumUtil {

    // Convert String to Enum (generic)
    public static <T extends Enum<T>> T toEnum(String value, Class<T> enumClass) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value '" + value + "' for enum " + enumClass.getSimpleName());
        }
    }

    // Convert Enum to String
    public static <T extends Enum<T>> String toString(T enumValue) {
        return enumValue == null ? null : enumValue.name();
    }
}
