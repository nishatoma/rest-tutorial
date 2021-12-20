package com.nisha.rest.tutorial.controllers;

import com.nisha.rest.tutorial.assembler.EmployeeModelAssembler;
import com.nisha.rest.tutorial.entities.Employee;
import com.nisha.rest.tutorial.exception.EmployeeNotFoundException;
import com.nisha.rest.tutorial.repositories.EmployeeRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Getting an aggregate 'root'
//    @GetMapping("/employees")
//    List<Employee> all() {
//        return repository.findAll();
//    }

    // Now getting an aggregate root 'resource'
    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

      // Old way of returning new Employee upon creating it.
//    @PostMapping("/employees")
//    public Employee addNewEmployee(@RequestBody Employee newEmployee) {
//        return repository.save(newEmployee);
//    }

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }


    @GetMapping("employees/{id}")
    public EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

      // Old way for updating employees
//    @PutMapping("/employees/{id}")
//    public Employee updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
//        return repository.findById(id)
//                .map(employee -> {
//                    employee.setName(newEmployee.getName());
//                    employee.setRole(newEmployee.getRole());
//                    return repository.save(employee);
//                }).orElseGet(() -> {
//                    newEmployee.setId(id);
//                    return repository.save(newEmployee);
//                });
//    }

    // New updated put mapping using ResponseEntity
    @PutMapping("/employees/{id}")
    ResponseEntity<?> updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

        // 1) Get the new employee
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                }).orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
        // 2) Convert the Employee to EntityModel using assembler
        EntityModel<Employee> employeeEntityModel = assembler.toModel(updatedEmployee);

        // 3) Return Response Entity
        return ResponseEntity
                .created(employeeEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(employeeEntityModel);
    }

      // Old Delete method
//    @DeleteMapping("/employees/{id}")
//    void deleteEmployee(@PathVariable Long id) {
//        repository.deleteById(id);
//    }

    // New DELETE method using Response Entity
    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
