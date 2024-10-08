package com.hanieum.llmproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.exception.errortype.CustomException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<Response<?>> handleDuplicateException(CustomException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		ex.printStackTrace();
		return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
	}
}
