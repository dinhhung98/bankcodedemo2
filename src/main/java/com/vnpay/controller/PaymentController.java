package com.vnpay.controller;

import com.vnpay.dto.BankRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @PostMapping
    public ResponseEntity<BankRequest>getResponse(){

    }
}
