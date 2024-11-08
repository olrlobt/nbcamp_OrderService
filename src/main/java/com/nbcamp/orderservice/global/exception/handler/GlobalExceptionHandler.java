package com.nbcamp.orderservice.global.exception.handler;

import java.nio.file.AccessDeniedException;

import javax.security.sasl.AuthenticationException;

import com.nbcamp.orderservice.global.exception.code.ErrorCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Setter
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Object>> handleIllegalException(IllegalArgumentException e) {
        return CommonResponse.fail(ErrorCode.VALIDATION_ERROR, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Object>> handleRuntimeException(RuntimeException e) {
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleException(Exception e) {
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
