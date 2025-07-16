package org.aub.payrollapi.service;


import org.aub.payrollapi.model.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees(Long page, Long size);
}
