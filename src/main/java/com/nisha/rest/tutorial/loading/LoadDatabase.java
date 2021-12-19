package com.nisha.rest.tutorial.loading;

import com.nisha.rest.tutorial.entities.Employee;
import com.nisha.rest.tutorial.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository) {
        return (args) -> {
            LOGGER.info("Saving Employee: " + employeeRepository.save(new Employee("Bilbo","Baggins", "burglar")));
            LOGGER.info("Saving Employee: " + employeeRepository.save(new Employee("Frodo","Baggins", "thief")));

        };
    }
}
