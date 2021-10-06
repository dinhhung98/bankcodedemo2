package com.vnpay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vnpay.dto.PaymentRequest;
import com.vnpay.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface PaymentService {
    public ResponseEntity<PaymentResponse> save(PaymentRequest bankRequest, String responseId) throws ParseException, JsonProcessingException;
}
