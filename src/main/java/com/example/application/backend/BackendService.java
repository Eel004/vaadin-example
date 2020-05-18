package com.example.application.backend;

import java.util.List;

import com.example.application.backend.domain.Employee;
import com.example.application.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackendService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public void saveEmployee(Employee aEmployee) {
        employeeRepository.save(aEmployee);
    }

}
