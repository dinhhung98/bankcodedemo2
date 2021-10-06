package com.vnpay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vnpay.dto.BankRequest;
import com.vnpay.dto.BankResponse;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface PaymentService {
    public ResponseEntity<BankResponse> save(BankRequest bankRequest, String responseId) throws ParseException, JsonProcessingException;
}
