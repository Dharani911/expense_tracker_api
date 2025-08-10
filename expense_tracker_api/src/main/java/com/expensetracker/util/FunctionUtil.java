package com.expensetracker.util;

import java.util.function.Consumer;

public class FunctionUtil {
    public static <T> void setIfPresent(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }
}
