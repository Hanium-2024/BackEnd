package com.hanieum.llmproject.model;

import java.util.Arrays;

public enum CategoryType {
    PLAN,
    DESIGN,
    CODE,
    TEST,
    DEPLOY;

    public static boolean isValid(String value) {
        return Arrays.stream(CategoryType.values())
                .anyMatch(category -> category.name().equalsIgnoreCase(value));
    }

    public static CategoryType fromString(String value) {
        return Arrays.stream(CategoryType.values())
                .filter(category -> category.name().equalsIgnoreCase(value))
                .findFirst()
                .get();
    }
}
