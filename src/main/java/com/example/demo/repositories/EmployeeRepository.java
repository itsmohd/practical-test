package com.example.demo.repositories;

import com.example.demo.entities.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepository {
    private final String jsonStorageDir = "data";
    private final String jsonStorageFile = "employees.json";
    private final String path;
    private final List<Employee> employees;

    /**
     * Constructs an EmployeeRepository.
     */
    public EmployeeRepository() {
        this.path = Path.of(jsonStorageDir, jsonStorageFile).toString();

        createJsonIfNotExists();

        this.employees = readEmployeesFromJson();
    }

    /**
     * Saves the provided employee entity to the repository.
     *
     * @param employee The employee entity to be saved.
     *
     * @return The saved employee entity.
     */
    public Employee save(Employee employee) {
        employee.id = generateNextId();
        employees.add(employee);

        saveEmployeesToJson();

        return employee;
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id The ID of the employee to find.
     *
     * @return The employee entity if found, or null if not found.
     */
    public Employee findById(int id) {
        Optional<Employee> foundEmployee = employees.stream()
                .filter(employee -> employee.id == id)
                .findFirst();

        return foundEmployee.orElse(null);
    }

    /**
     * Retrieves all employees based on optional filtering criteria.
     *
     * @param name The name to filter by (optional).
     * @param fromSalary The minimum salary to filter by (optional).
     * @param toSalary The maximum salary to filter by (optional).
     *
     * @return A list of employees that match the filtering criteria.
     */
    public List<Employee> all(String name, Double fromSalary, Double toSalary) {
        return employees.stream()
                .filter(employee -> (name == null ||
                        employee.firstName.toLowerCase().contains(name.toLowerCase()) || employee.lastName.toLowerCase().contains(name.toLowerCase())) &&
                        (fromSalary == null || employee.salary >= fromSalary) &&
                        (toSalary == null || employee.salary <= toSalary))
                .collect(Collectors.toList());
    }

    /**
     * Reads employee data from the JSON file.
     *
     * @return A list of employees read from the JSON file.
     */
    private List<Employee> readEmployeesFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(Path.of(path).toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Writes employee data to the JSON file.
     */
    private void saveEmployeesToJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(Path.of(path).toFile(), employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the next ID for a new employee.
     *
     * @return The next available ID for a new employee.
     */
    private int generateNextId() {
        return employees.size() + 1;
    }

    /**
     * Creates the JSON file if it does not exist.
     */
    private void createJsonIfNotExists() {
        try {
            Path directoryPath = Path.of(this.jsonStorageDir).toAbsolutePath().normalize();
            Files.createDirectories(directoryPath);
            Path target = directoryPath.resolve(this.jsonStorageFile);

            if (!Files.exists(target)) {
                Files.writeString(target, "[]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
