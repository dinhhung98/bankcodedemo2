package com.vnpay.dto;

public class BankResponse {
    private String code;
    private String message;
    private String responseId;
    private String checkSum;
    private String addValue;

    public BankResponse(String code, String message, String responseId, String checkSum, String addValue) {
        this.code = code;
        this.message = message;
        this.responseId = responseId;
        this.checkSum = checkSum;
        this.addValue = addValue;
    }

    public BankResponse(String code, String message,String responseId) {
        this.code = code;
        this.message = message;
        this.responseId = responseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getAddValue() {
        return addValue;
    }

    public void setAddValue(String addValue) {
        this.addValue = addValue;
    }

    @Override
    public String toString() {
        return "BankResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", responseId='" + responseId + '\'' +
                ", checkSum='" + checkSum + '\'' +
                ", addValue='" + addValue + '\'' +
                '}';
    }
}
