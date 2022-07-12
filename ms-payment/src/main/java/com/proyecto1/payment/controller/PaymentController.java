package com.proyecto1.payment.controller;

import com.proyecto1.payment.entity.Payment;
import com.proyecto1.payment.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/findAll")
    public Flux<Payment> getPayments(){
        return paymentService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Payment> getPayment(@PathVariable String id){
        Mono<Payment> newDeposit = paymentService.findById(id);
        return newDeposit;
    }

    @PostMapping("/create")
    public Mono<Payment> createPayment(@RequestBody Payment c){
        Mono<Payment> newDeposit = paymentService.create(c);
        return newDeposit;
    }

    @PutMapping("/update/{id}")
    public Mono<Payment> updatePayment(@RequestBody Payment c, @PathVariable String id){
        return paymentService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Payment> deletePayment(@PathVariable String id){
                return paymentService.delete(id);
    }


}
