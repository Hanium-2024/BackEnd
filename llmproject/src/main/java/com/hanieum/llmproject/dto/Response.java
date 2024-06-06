package com.hanieum.llmproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)         // null 값을 가지는 필드는 Json 응답에 미포함
@AllArgsConstructor
@Getter
public class Response<T> {
    private String success;
    private String message;
    private T data;

    public Response(String success, String message) {
        this(success, message,null);
    }
}
