package com.proyecto1.product.controller;

import com.proyecto1.product.entity.Product;
import com.proyecto1.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/findAll")
    public Flux<Product> getCustomers(){
        return productService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Product> getCustomer(@PathVariable String id){
        Mono<Product> newCustomer = productService.findById(id);
        return newCustomer;
    }

    @PostMapping("/create")
    public Mono<Product> createCustomer(@RequestBody Product p){
        Mono<Product> newCustomer = productService.create(p);
        return newCustomer;
    }

    @PutMapping("/update/{id}")
    public Mono<Product> updateCustomer(@RequestBody Product p, @PathVariable String id){
        return productService.update(p,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Product> deleteCustomer(@PathVariable String id){
        return productService.delete(id);
    }

}
