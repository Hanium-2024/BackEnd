package com.hanieum.llmproject.model;

import java.util.Arrays;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;

public enum Category {
	PLAN,
	DESIGN,
	CODE,
	RETROSPECT;

	public static boolean isValid(String value) {
		return Arrays.stream(Category.values())
			.anyMatch(category -> category.name().equalsIgnoreCase(value));
	}

	public static Category fromString(String value) {
		return Arrays.stream(Category.values())
			.filter(category -> category.name().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_VALID));
	}
}
