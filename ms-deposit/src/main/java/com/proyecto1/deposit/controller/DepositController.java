package com.proyecto1.deposit.controller;

import com.proyecto1.deposit.entity.Deposit;
import com.proyecto1.deposit.service.DepositService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @GetMapping("/findAll")
    public Flux<Deposit> getDeposits(){
        return depositService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Deposit> getDeposit(@PathVariable String id){
        Mono<Deposit> newDeposit = depositService.findById(id);
        return newDeposit;
    }

    @PostMapping("/create")
    public Mono<Deposit> createDeposit(@RequestBody Deposit c){
        Mono<Deposit> newDeposit = depositService.create(c);
        return newDeposit;
    }

    @PutMapping("/update/{id}")
    public Mono<Deposit> updateDeposit(@RequestBody Deposit c, @PathVariable String id){
        return depositService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Deposit> deleteDeposit(@PathVariable String id){
                return depositService.delete(id);
    }


}
