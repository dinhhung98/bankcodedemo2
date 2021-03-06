package com.vnpay.exception;

import com.vnpay.constance.ResponseCode;
import com.vnpay.constance.ResponseMessage;
import com.vnpay.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BankException {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.DATA_REQUEST_ERROR, ResponseMessage.MSG_DATA_REQUEST_ERROR,"","",""));
    }
}
