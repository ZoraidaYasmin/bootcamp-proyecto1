package com.proyecto1.customer.service.impl;

import com.proyecto1.customer.entity.Customer;
import com.proyecto1.customer.repository.CustomerRepository;
import com.proyecto1.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Flux<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> create(Customer c) {
        return customerRepository.save(c);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Mono<Customer> update(Customer c, String id) {
        return customerRepository.findById(id)
                .map( x -> {
                    x.setName(c.getName());
                    x.setLastName(c.getLastName());
                    x.setDocNumber(c.getDocNumber());
                    x.setTypeCustomer(c.getTypeCustomer());
                    x.setDescTypeCustomer(c.getDescTypeCustomer());
                    return x;
                }).flatMap(customerRepository::save);
    }

    @Override
    public Mono<Customer> delete(String id) {
        return customerRepository.findById(id).flatMap( x -> customerRepository.delete(x).then(Mono.just(new Customer())));
    }
}
