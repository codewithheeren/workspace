package com.codewithheeren.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
//http://localhost:8100/currency-converter/from/USD/to/INR/quantity/5
//http://localhost:8000/currency-exchange/from/USD/to/INR
public class SpringCloudApiGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudApiGatewayServerApplication.class, args);
	}

}
