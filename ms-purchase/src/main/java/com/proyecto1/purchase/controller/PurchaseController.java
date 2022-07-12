package com.proyecto1.purchase.controller;

import com.proyecto1.purchase.entity.Purchase;
import com.proyecto1.purchase.service.PurchaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/findAll")
    public Flux<Purchase> getPurchases(){
        return purchaseService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Purchase> getPurchase(@PathVariable String id){
        Mono<Purchase> newDeposit = purchaseService.findById(id);
        return newDeposit;
    }

    @PostMapping("/create")
    public Mono<Purchase> createPurchase(@RequestBody Purchase c){
        Mono<Purchase> newDeposit = purchaseService.create(c);
        return newDeposit;
    }

    @PutMapping("/update/{id}")
    public Mono<Purchase> updatePurchase(@RequestBody Purchase c, @PathVariable String id){
        return purchaseService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Purchase> deletePurchase(@PathVariable String id){
                return purchaseService.delete(id);
    }


}
