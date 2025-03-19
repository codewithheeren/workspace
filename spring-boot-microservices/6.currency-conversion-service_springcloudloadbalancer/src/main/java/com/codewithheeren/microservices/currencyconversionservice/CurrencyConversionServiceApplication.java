package com.codewithheeren.microservices.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * This project implements spring cloud load balancer.
 * prerequisite - execute multiple instances of currency exchange service.
 * VM Args for currency exchange service eg.:  --server.port=8200
 * @author Heeren
 * @version 1.0
 */
@SpringBootApplication
@EnableFeignClients("com.codewithheeren.microservices.currencyconversionservice")
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}
}
