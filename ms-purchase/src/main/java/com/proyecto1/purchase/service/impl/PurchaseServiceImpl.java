package com.proyecto1.purchase.service.impl;

import com.proyecto1.purchase.entity.Purchase;
import com.proyecto1.purchase.repository.PurchaseRepository;
import com.proyecto1.purchase.service.PurchaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Override
    public Flux<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public Mono<Purchase> create(Purchase c) {
        return purchaseRepository.save(c);
    }

    @Override
    public Mono<Purchase> findById(String id) {
        return purchaseRepository.findById(id);
    }

    @Override
    public Mono<Purchase> update(Purchase c, String id) {
        return purchaseRepository.findById(id)
                .map( x -> {
                    x.setDate(c.getDate());
                    x.setPurchaseAmount(c.getPurchaseAmount());
                    x.setDescription(c.getDescription());
                    return x;
                }).flatMap(purchaseRepository::save);
    }

    @Override
    public Mono<Purchase> delete(String id) {
        return purchaseRepository.findById(id).flatMap( x -> purchaseRepository.delete(x).then(Mono.just(new Purchase())));
    }
}
