package org.aub.payrollapi.respository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.aub.payrollapi.model.entity.Employee;

import java.util.List;

@Mapper
public interface EmployeeRepository {
    @Select("""
        SELECT * FROM employees
        ORDER BY created_at DESC
        LIMIT #{size} OFFSET #{offset}
    """)
    List<Employee> getAllEmployees(@Param("offset") Long offset, @Param("size") Long size);
}
