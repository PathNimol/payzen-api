package org.aub.payrollapi.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aub.payrollapi.model.enums.EmployeeStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest{
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate dateJoined;
    private String department;
    private UUID positionId;
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
}
