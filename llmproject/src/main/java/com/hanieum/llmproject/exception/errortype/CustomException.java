package com.hanieum.llmproject.exception.errortype;

import com.hanieum.llmproject.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * com.hanieum.llmproject.exception.ErrorCode 의 값을 매개변수로
 * 생성자 호출 필요
 *
 */
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
