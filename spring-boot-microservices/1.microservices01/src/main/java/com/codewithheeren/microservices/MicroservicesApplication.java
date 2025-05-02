package com.codewithheeren.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.codewithheeren.microservices.service.EmployeeService;

@SpringBootApplication
// Running on Port 8181- Rest Template Implementation.
// Consuming Microservice02 Rest endpoints using RestTemplate. 
public class MicroservicesApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MicroservicesApplication.class, args);
        EmployeeService service = context.getBean(EmployeeService.class);

        System.out.println("Microservice-1 ...");

        // Step 1: Create a new employee
        service.createEmployee();

        // Step 2: Get newly created employee
        service.getEmployeeById(3L);

        // Step 3: Get all employees
        service.getAllEmployees();

        // Step 4: Update employee
        service.updateEmployee(3L);

        // Step 5: Delete employee
        service.deleteEmployee(3L);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
