package org.aub.payrollapi.model.entity;

import lombok.*;
import org.aub.payrollapi.model.enums.EmployeeStatus;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private UUID employeeId;
    private AppUser manager;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate dateJoined;
    private String department;
    private UUID positionId;
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    private LocalDateTime createdAt = LocalDateTime.now();
}
