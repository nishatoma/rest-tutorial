package com.nisha.rest.tutorial.repositories;

import com.nisha.rest.tutorial.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
