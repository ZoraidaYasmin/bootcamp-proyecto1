package com.proyecto1.withdrawal.service.impl;

import com.proyecto1.withdrawal.entity.Withdrawal;
import com.proyecto1.withdrawal.repository.WithdrawalRepository;
import com.proyecto1.withdrawal.service.WithdrawalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    WithdrawalRepository withdrawalRepository;

    @Override
    public Flux<Withdrawal> findAll() {
        return withdrawalRepository.findAll();
    }

    @Override
    public Mono<Withdrawal> create(Withdrawal c) {
        return withdrawalRepository.save(c);
    }

    @Override
    public Mono<Withdrawal> findById(String id) {
        return withdrawalRepository.findById(id);
    }

    @Override
    public Mono<Withdrawal> update(Withdrawal c, String id) {
        return withdrawalRepository.findById(id)
                .map( x -> {
                    x.setDate(c.getDate());
                    x.setWithdrawalAmount(c.getWithdrawalAmount());
                    x.setDescription(c.getDescription());
                    return x;
                }).flatMap(withdrawalRepository::save);
    }

    @Override
    public Mono<Withdrawal> delete(String id) {
        return withdrawalRepository.findById(id).flatMap( x -> withdrawalRepository.delete(x).then(Mono.just(new Withdrawal())));
    }
}
