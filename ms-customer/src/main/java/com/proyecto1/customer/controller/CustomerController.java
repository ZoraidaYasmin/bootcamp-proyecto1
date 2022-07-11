package com.proyecto1.customer.controller;

import com.proyecto1.customer.entity.Customer;
import com.proyecto1.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/findAll")
    public Flux<Customer> getCustomers(){
        return customerService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Customer> getCustomer(@PathVariable String id){
        Mono<Customer> newCustomer = customerService.findById(id);
        return newCustomer;
    }

    @PostMapping("/create")
    public Mono<Customer> createCustomer(@RequestBody Customer c){
        Mono<Customer> newCustomer = customerService.create(c);
        return newCustomer;
    }

    @PutMapping("/update/{id}")
    public Mono<Customer> updateCustomer(@RequestBody Customer c, @PathVariable String id){
        return customerService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Customer> deleteCustomer(@PathVariable String id){
                return customerService.delete(id);
    }


}
