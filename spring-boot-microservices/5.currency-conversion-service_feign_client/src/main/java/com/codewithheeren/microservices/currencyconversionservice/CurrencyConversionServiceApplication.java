package com.codewithheeren.microservices.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.codewithheeren.microservices.currencyconversionservice")
/**
 * This project implements Feign client.
 * CurrencyConversionService (Microservice1) is client class which is consuming CurrencyExcehangeService (Microservice 2).
 * CurrencyConversionService having a proxy service implemented feign client to simplifies REST APIs call of existing CurrencyExcehangeService.
 * CurrencyConversionService getting rates from CurrencyExcehangeService and then calculating total conversion amount basis on quantity.
 * @author Heeren
 * @version 1.0
 */
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}
}
