package com.vnpay.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.hash.Hashing;
import com.vnpay.config.ConfigBankYaml;
import com.vnpay.constance.ResponseCode;
import com.vnpay.constance.ResponseMessage;
import com.vnpay.dto.PaymentRequest;
import com.vnpay.dto.PaymentResponse;
import com.vnpay.model.Bank;
import com.vnpay.service.PaymentService;
import com.vnpay.util.JedisUtil;
import com.vnpay.util.MapperObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@Service
public class ServiceImp implements PaymentService {
    @Autowired
    private ConfigBankYaml bankYaml;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private AmqpTemplate rabbit;
    @Value("${vnpay.rabbitmq.exchange}")
    private String exchange;

    @Value("${vnpay.rabbitmq.routingkey}")
    private String routingkey;

    private static Logger log = LogManager.getLogger(ServiceImp.class);

    @Override
    public ResponseEntity<PaymentResponse> save(PaymentRequest bankRequest, String responseId) throws ParseException, JsonProcessingException {
        String privateKey = findBankByBankCode(bankRequest.getBankCode());
        if (Strings.isEmpty(privateKey)){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.BANK_CODE_ERROR, ResponseMessage.MSG_BANK_CODE_ERROR,responseId));
        }
        if (!isCheckSum(bankRequest,privateKey)){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.CHECK_SUM_ERROR, ResponseMessage.MSG_CHECK_SUM_ERROR,responseId));
        }
        if (!isPayDate(bankRequest.getPayDate())){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.DATA_REQUEST_ERROR, ResponseMessage.MSG_PAY_DATE_ERROR,responseId));
        }
        if (findTokenKey(bankRequest.getTokenKey())){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.TOKEN_EXITS, ResponseMessage.MSG_TOKEN_EXITS,responseId));
        }
        if (!checkAmount(bankRequest)){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.AMOUNT_ERROR, ResponseMessage.MSG_AMOUNT_ERROR,responseId));
        }
        if (!saveDataToRedis(bankRequest)){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.SAVE_ERROR, ResponseMessage.MSG_SAVE_ERROR,responseId));
        }
        String message =sendQueue(bankRequest);
        if ("200".equals(message)){
            return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.CODE_SUCCESS, ResponseMessage.MSG_SUCCESS,responseId,bankRequest.getCheckSum(),bankRequest.getAddValue()));
        }
        return ResponseEntity.badRequest().body(new PaymentResponse(ResponseCode.SEND_TO_RABBIT_ERROR, ResponseMessage.MSG_RABBIT_ERROR,responseId));
    }

    private String findBankByBankCode(String bankCode) {
        log.info("Begin find privateKey with bank code {}", bankCode);
        ArrayList<Bank> banks = (ArrayList<Bank>) bankYaml.getListBank();
        for (Bank bank : banks){
            if (bank.getBankCode().equals(bankCode)) return bank.getPrivateKey();
        }
        return "";
    }

    private boolean isCheckSum(PaymentRequest bankRequest, String privateKey) {
        String tempCheck = bankRequest.getMobile() + bankRequest.getBankCode() + bankRequest.getAccountNo()
                + bankRequest.getPayDate() + bankRequest.getDebitAmount() + bankRequest.getRespCode()
                + bankRequest.getTraceTransfer() + bankRequest.getMessageType() + privateKey;
        log.info("Data before check sum {}",tempCheck);
        String sha256 = Hashing.sha256().hashString(tempCheck, StandardCharsets.UTF_8).toString();
        log.info("Data after check sum {}",sha256);

        if (bankRequest.getCheckSum().equals(sha256)) {
            return true;
        }
        return false;
    }

    private boolean isPayDate(String payDate) {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setLenient(false);
        try {
            Date payDateInput = sdf.parse(payDate);
            log.info("Pay Date: {}",payDateInput.toString());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean saveDataToRedis(PaymentRequest paymentRequestApi) {
        try {
            jedisUtil.save(paymentRequestApi.getTokenKey(), paymentRequestApi.toString());
            Long timeExpire = Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()).getTime();
            jedisUtil.expire(paymentRequestApi.getTokenKey(), timeExpire);
            return true;
        } catch (Exception e) {
            log.error("Save Data Redis: ", e);
            return false;
        }
    }

    public String sendQueue(PaymentRequest paymentRequestApi) throws JsonProcessingException, AmqpException {
        log.info("Begin send data to rabbit: {}", paymentRequestApi);
        try {
            String messsage = (String) rabbit.convertSendAndReceive(exchange, routingkey, MapperObject.getMapperObject().objectToJson(paymentRequestApi));
            log.info("Response from rabbit: {}", messsage);
            return messsage;
        }
        catch (Exception e){
            log.error("Received response from rabbit",e);
            return "";
        }
    }

    private boolean findTokenKey(String tokenKey) {
        return jedisUtil.exits(tokenKey);
    }

    private boolean checkAmount(PaymentRequest bankRequest){
        log.info("Real amount: {} and debit amount: {}",bankRequest.getRealAmount(),bankRequest.getDebitAmount());
        if (Strings.isEmpty(bankRequest.getRealAmount().toString())) return false;
        int compareAmount = bankRequest.getRealAmount().compareTo(bankRequest.getDebitAmount());
        if (compareAmount > 0){
            return false;
        }
        return true;
    }
}
