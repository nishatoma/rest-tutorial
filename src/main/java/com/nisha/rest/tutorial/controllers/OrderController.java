package com.nisha.rest.tutorial.controllers;

import com.nisha.rest.tutorial.assembler.OrderModelAssembler;
import com.nisha.rest.tutorial.entities.Order;
import com.nisha.rest.tutorial.enums.Status;
import com.nisha.rest.tutorial.exception.OrderNotFoundException;
import com.nisha.rest.tutorial.repositories.OrderRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    private OrderRepository repository;
    private OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {

        List<EntityModel<Order>> orders = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());

    }

    @GetMapping("orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {

        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> newOrder(@RequestBody Order order) {

        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = repository.save(order);

        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                .body(assembler.toModel(newOrder));
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        // 1) Fetch the order that already exists
        Order orderToCancel = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        // If the order is in progress, we can cancel it
        if (orderToCancel.getStatus() == Status.IN_PROGRESS) {
            orderToCancel.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(repository.save(orderToCancel)));
        }

        // Otherwise if the order is already cancelled or completed...
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't cancel an order that is in status of " + orderToCancel.getStatus()));
    }

    // Mark an order as complete
    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {

        // Find the order with the ID first
        Order orderToComplete = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // If the order is in progress, then update status to complete
        if (orderToComplete.getStatus() == Status.IN_PROGRESS) {
            // Set status to complete
            orderToComplete.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(orderToComplete)));
        }

        // Otherwise, the order is already cancelled or completed
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't cancel an order that is in " + orderToComplete.getStatus() + " status."));
    }
}
