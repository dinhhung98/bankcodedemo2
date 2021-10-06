package com.vnpay.model;

import lombok.Data;

@Data
public class Bank {
    private String bankCode;
    private String privateKey;
    private String ips;
    private String status;

    @Override
    public String toString() {
        return "Bank{" +
                "bankCode='" + bankCode + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", ips='" + ips + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
