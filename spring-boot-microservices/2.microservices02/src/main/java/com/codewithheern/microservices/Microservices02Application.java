package com.codewithheern.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.codewithheeren")
public class Microservices02Application {

	public static void main(String[] args) {
		SpringApplication.run(Microservices02Application.class, args);
		System.out.println("Microservice-2 ...");
	}

}
