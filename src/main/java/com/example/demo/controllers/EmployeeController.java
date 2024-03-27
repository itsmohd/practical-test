package com.example.demo.controllers;

import com.example.demo.entities.Employee;
import com.example.demo.repositories.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("employees")
public class EmployeeController {
    EmployeeRepository employeeRepository;

    /**
     * Constructs an EmployeeController.
     */
    @Autowired
    public EmployeeController(EmployeeRepository employeeService) {
        this.employeeRepository = employeeService;
    }

    /**
     * Handle retrieving and filtering employees list.
     *
     * @param name The name to filter by (optional).
     * @param fromSalary The minimum salary to filter by (optional).
     * @param toSalary The maximum salary to filter by (optional).
     *
     * @return A list of employees that match the filtering criteria.
     */
    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double fromSalary,
            @RequestParam(required = false) Double toSalary
    ) {
        List<Employee> employees = this.employeeRepository.all(name, fromSalary, toSalary);

        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

    /**
     * Handle retrieving employee by ID.
     *
     * @param id The ID of the employee to retrieve.
     *
     * @return The employee if found, or HTTP 404 response if the employee does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable int id) {
        Employee employee = this.employeeRepository.findById(id);

        if (employee != null) {
            return ResponseEntity.status(HttpStatus.OK).body(employee);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Handle creating a new employee request.
     *
     * @param payload The employee data to be saved.
     *
     * @return ID of the newly created employee.
     */
    @PostMapping
    public ResponseEntity<?> store(@RequestBody @Valid Employee payload) {
        Employee employee = this.employeeRepository.save(payload);

        Map<String, Object> response = new HashMap<>();
        response.put("id", employee.id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
