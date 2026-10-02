package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse> handleBase(BaseException e) {
        return ResponseEntity
                .status(e.getErrorCode())
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException() {
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error());
    }
}
