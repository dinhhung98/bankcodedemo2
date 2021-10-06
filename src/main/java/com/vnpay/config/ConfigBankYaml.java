package com.vnpay.config;

import com.vnpay.model.Bank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "banks")
@PropertySource(value =  "classpath:bank.yml",factory = YamlPropertySourceFactory.class)
@Data
public class ConfigBankYaml {
    private List<Bank> listBank;
}
