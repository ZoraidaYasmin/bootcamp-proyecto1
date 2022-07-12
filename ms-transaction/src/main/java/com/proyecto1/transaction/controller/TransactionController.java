package com.proyecto1.transaction.controller;

import com.proyecto1.transaction.entity.Transaction;
import com.proyecto1.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/findAll")
    public Flux<Transaction> getAccounts(){
        return transactionService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Transaction> getAccount(@PathVariable String id){
        Mono<Transaction> newAccount = transactionService.findById(id);
        return newAccount;
    }
    @GetMapping("/findWithCustomer/{id}")
    public Mono<Transaction> getAccountWithCustomer(@PathVariable String id){
        Mono<Transaction> newAccount = transactionService.findByIdWithCostumer(id);
        return newAccount;
    }
    @PostMapping("/create")
    public Mono<Transaction> createAccount(@RequestBody Transaction t){
        Mono<Transaction> newAccount = transactionService.save(t);
        return newAccount;
    }

    @PutMapping("/update/{id}")
    public Mono<Transaction> updateAccount(@RequestBody Transaction t, @PathVariable String id){
        return transactionService.update(t,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Transaction> deleteAccount(@PathVariable String id){
        return transactionService.delete(id);
    }
}
