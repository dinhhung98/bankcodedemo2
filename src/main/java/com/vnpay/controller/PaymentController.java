package com.vnpay.controller;

import com.vnpay.dto.PaymentRequest;
import com.vnpay.dto.PaymentResponse;
import com.vnpay.service.imp.ServiceImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class PaymentController {
    @Autowired
    ServiceImp bankService;
    private static Logger log = LogManager.getLogger(PaymentController.class);
    @PostMapping
    public ResponseEntity<PaymentResponse>getPaymentRequest(@Valid @RequestBody PaymentRequest bankRequest) throws Exception{
        String tokenRequest = UUID.randomUUID().toString();
        ThreadContext.put("token", tokenRequest);
        log.info("Token request {}",tokenRequest);
        log.info("Payment request {}",bankRequest.toString());
        try {
            return bankService.save(bankRequest,tokenRequest);
        }finally {
            ThreadContext.pop();
            ThreadContext.clearMap();
        }
    }
}
