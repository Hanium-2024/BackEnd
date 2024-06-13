package com.hanieum.llmproject.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 관련 에러 핸들러
 *
 */
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (MalformedJwtException e) {
            log.error("지원되지 않는 형식의 토큰입니다");
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 토큰이 입력되었습니다.");
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다");
            setErrorResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        Response.fail(errorCode.getMessage())
                )
        );
    }
}
