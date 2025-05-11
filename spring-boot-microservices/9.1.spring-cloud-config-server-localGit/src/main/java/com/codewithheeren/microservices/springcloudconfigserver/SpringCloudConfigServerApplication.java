package com.codewithheeren.microservices.springcloudconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
//spring cloud config server 
//http://localhost:8888/propertyfilename/default
//http://localhost:8888/propertyfilename/dev
//http://localhost:8888/propertyfilename/qa
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}
}
