package com.proyecto1.deposit.service.impl;

import com.proyecto1.deposit.entity.Deposit;
import com.proyecto1.deposit.repository.DepositRepository;
import com.proyecto1.deposit.service.DepositService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    DepositRepository depositRepository;

    @Override
    public Flux<Deposit> findAll() {
        return depositRepository.findAll();
    }

    @Override
    public Mono<Deposit> create(Deposit c) {
        return depositRepository.save(c);
    }

    @Override
    public Mono<Deposit> findById(String id) {
        return depositRepository.findById(id);
    }

    @Override
    public Mono<Deposit> update(Deposit c, String id) {
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
        return depositRepository.findById(id).flatMap( x -> depositRepository.delete(x).then(Mono.just(new Deposit())));
    }
}
