package com.proyecto1.deposit.service.impl;

import com.proyecto1.deposit.entity.Deposit;
import com.proyecto1.deposit.repository.DepositRepository;
import com.proyecto1.deposit.service.DepositService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DepositServiceImpl implements DepositService {

    private static final Logger log = LogManager.getLogger(DepositServiceImpl.class);
    @Autowired
    DepositRepository depositRepository;

    @Override
    public Flux<Deposit> findAll() {
        log.info("Method call FindAll - deposit");
        return depositRepository.findAll();
    }

    @Override
    public Mono<Deposit> create(Deposit c) {
        log.info("Method call create - deposit");
        return depositRepository.save(c);
    }

    @Override
    public Mono<Deposit> findById(String id) {
        log.info("Method call findById - deposit");
        return depositRepository.findById(id);
    }

    @Override
    public Mono<Deposit> update(Deposit c, String id) {
        log.info("Method call update - deposit");
        return depositRepository.findById(id)
                .map( x -> {
                    x.setDate(c.getDate());
                    x.setDepositAmount(c.getDepositAmount());
                    x.setDescription(c.getDescription());
                    return x;
                }).flatMap(depositRepository::save);
    }

    @Override
    public Mono<Deposit> delete(String id) {
        log.info("Method call delete - deposit");
        return depositRepository.findById(id).flatMap( x -> depositRepository.delete(x).then(Mono.just(new Deposit())));
    }
}
