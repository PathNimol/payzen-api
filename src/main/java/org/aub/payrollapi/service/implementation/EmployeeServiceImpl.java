package org.aub.payrollapi.service.implementation;

import lombok.RequiredArgsConstructor;
import org.aub.payrollapi.model.entity.Employee;
import org.aub.payrollapi.respository.EmployeeRepository;
import org.aub.payrollapi.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    @Override
    public List<Employee> getAllEmployees(Long page, Long size) {
        page = (page - 1) * size;
        return employeeRepository.getAllEmployees(page, size);
    }
}
