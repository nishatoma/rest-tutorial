package com.nisha.rest.tutorial.repositories;

import com.nisha.rest.tutorial.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
