package com.codewithheeren.microservices.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.codewithheeren.microservices.entity.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final List<Employee> employees = new ArrayList<>();

	public EmployeeController() {
		employees.add(new Employee(1, "Heeren", "S.", "heeren@gmail.com"));
		employees.add(new Employee(2, "Ravi", "Kumar", "ravi@gmail.com"));
	}

	@GetMapping
	public List<Employee> getAllEmployees() {
		System.out.println("Microservice2 getAllEmployees(..) invoked");
		return employees;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
		System.out.println("Microservice2 getEmployeeById(..) invoked with Id - " + id);
		Optional<Employee> employee = employees.stream().filter(emp -> emp.getId() == id).findFirst();

		return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
		System.out.println("Microservice2 createEmployee(..) invoked with employee - " + employee);
		employees.add(employee);
		return new ResponseEntity<>(employee, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
		System.out.println("Microservice2 updateEmployee(..) invoked");

		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i).getId() == id) {
				employees.set(i, updatedEmployee);
				return ResponseEntity.ok(updatedEmployee);
			}
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		System.out.println("Microservice2 deleteEmployee(..) invoked with ID: " + id);
		if (employees.removeIf(emp -> emp.getId() == id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
