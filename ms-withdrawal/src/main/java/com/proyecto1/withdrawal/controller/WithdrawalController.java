package com.proyecto1.withdrawal.controller;

import com.proyecto1.withdrawal.entity.Withdrawal;
import com.proyecto1.withdrawal.service.WithdrawalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/withdrawal")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @GetMapping("/findAll")
    public Flux<Withdrawal> getWithdrawals(){
        return withdrawalService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Withdrawal> getWithdrawal(@PathVariable String id){
        Mono<Withdrawal> newDeposit = withdrawalService.findById(id);
        return newDeposit;
    }

    @PostMapping("/create")
    public Mono<Withdrawal> createWithdrawal(@RequestBody Withdrawal c){
        Mono<Withdrawal> newDeposit = withdrawalService.create(c);
        return newDeposit;
    }

    @PutMapping("/update/{id}")
    public Mono<Withdrawal> updateWithdrawal(@RequestBody Withdrawal c, @PathVariable String id){
        return withdrawalService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Withdrawal> deleteWithdrawal(@PathVariable String id){
                return withdrawalService.delete(id);
    }


}
