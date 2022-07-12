package com.proyecto1.purchase.service.impl;

import com.proyecto1.purchase.entity.Purchase;
import com.proyecto1.purchase.repository.PurchaseRepository;
import com.proyecto1.purchase.service.PurchaseService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger log = LogManager.getLogger(PurchaseServiceImpl.class);
    @Autowired
    PurchaseRepository purchaseRepository;

    @Override
    public Flux<Purchase> findAll() {
        log.info("Method call FindAll - purchase");
        return purchaseRepository.findAll();
    }

    @Override
    public Mono<Purchase> create(Purchase c) {
        log.info("Method call create - purchase");
        return purchaseRepository.save(c);
    }

    @Override
    public Mono<Purchase> findById(String id) {
        log.info("Method call FindById - purchase");
        return purchaseRepository.findById(id);
    }

    @Override
    public Mono<Purchase> update(Purchase c, String id) {
        log.info("Method call Update - purchase");
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
        log.info("Method call delete - purchase");
        return purchaseRepository.findById(id).flatMap( x -> purchaseRepository.delete(x).then(Mono.just(new Purchase())));
    }
}
