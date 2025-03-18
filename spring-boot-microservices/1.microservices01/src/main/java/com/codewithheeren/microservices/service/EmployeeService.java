package com.codewithheeren.microservices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.codewithheeren.microservices.entity.EmployeeDTO;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeService {

	@Autowired
    private final RestTemplate restTemplate = null;
    private static final String BASE_URL = "http://localhost:8080/api/v1/employees";

    public void getAllEmployees() {
        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL, HttpMethod.GET, createHttpEntity(), String.class);

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        String url = BASE_URL + "/{id}";
        ResponseEntity<EmployeeDTO> response = restTemplate.exchange(
            url, HttpMethod.GET, createHttpEntity(), EmployeeDTO.class, id);

        EmployeeDTO employee = response.getBody();
        System.out.println("Fetched Employee: " + employee);
        return employee;
    }

    public void createEmployee() {
        EmployeeDTO newEmployee = new EmployeeDTO(3L, "Tom", "Z.", "tom@gmail.com");
        ResponseEntity<EmployeeDTO> response = restTemplate.postForEntity(
            BASE_URL, newEmployee, EmployeeDTO.class);

        System.out.println("Employee Created: " + response.getBody());
    }

    public void updateEmployee(Long id) {
        String url = BASE_URL + "/{id}";
        EmployeeDTO updatedEmployee = new EmployeeDTO(id, "Updated", "Name", "updated@gmail.com");
        restTemplate.put(url, updatedEmployee, id);
        System.out.println("Employee Updated: " + updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        String url = BASE_URL + "/{id}";
        restTemplate.delete(url, id);
        System.out.println("Employee Deleted with ID: " + id);
    }

    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }
}
