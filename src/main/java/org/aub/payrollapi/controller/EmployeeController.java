package org.aub.payrollapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.aub.payrollapi.base.ApiResponse;
import org.aub.payrollapi.base.BaseController;
import org.aub.payrollapi.model.entity.Employee;
import org.aub.payrollapi.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController extends BaseController {
    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all Employees")
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees(@RequestParam(defaultValue = "1") Long page , @RequestParam(defaultValue = "10") Long size) {
        return response("All employees fetch successfully!", employeeService.getAllEmployees(page,size));
    }
}
