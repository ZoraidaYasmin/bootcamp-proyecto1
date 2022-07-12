package com.proyecto1.signatory.controller;

import com.proyecto1.signatory.entity.Signatory;
import com.proyecto1.signatory.service.SignatoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/signatory")
public class SignatoryController {

    @Autowired
    private SignatoryService signatoryService;

    @GetMapping("/findAll")
    public Flux<Signatory> getSignatories(){
        return signatoryService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Signatory> getSignatory(@PathVariable String id){
        Mono<Signatory> newDeposit = signatoryService.findById(id);
        return newDeposit;
    }

    @PostMapping("/create")
    public Mono<Signatory> createSignatory(@RequestBody Signatory c){
        Mono<Signatory> newDeposit = signatoryService.create(c);
        return newDeposit;
    }

    @PutMapping("/update/{id}")
    public Mono<Signatory> updateSignatory(@RequestBody Signatory c, @PathVariable String id){
        return signatoryService.update(c,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Signatory> deleteSignatory(@PathVariable String id){
                return signatoryService.delete(id);
    }


}
