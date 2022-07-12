package com.proyecto1.transaction.service.impl;

import com.proyecto1.transaction.client.CustomerClient;
import com.proyecto1.transaction.entity.Transaction;
import com.proyecto1.transaction.repository.TransactionRepository;
import com.proyecto1.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransacionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CustomerClient customerClient;

    @Override
    public Flux<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> save(Transaction t) {
        return transactionRepository.save(t);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> update(Transaction t, String id) {
        return transactionRepository.findById(id)
                .map( x -> {
                    x.setProductId(t.getProductId());
                    x.setAccountNumber(t.getAccountNumber());
                    x.setMovementLimit(t.getMovementLimit());
                    x.setCreditLimit(t.getCreditLimit());
                    x.setAvailableBalance(t.getAvailableBalance());
                    x.setMaintenanceCommission(t.getMaintenanceCommission());
                    x.setCardNumber(t.getCardNumber());
                    return x;
                }).flatMap(transactionRepository::save);
    }

    @Override
    public Mono<Transaction> delete(String id) {
        return transactionRepository.findById(id).flatMap( x -> transactionRepository.delete(x).then(Mono.just(new Transaction())));
    }

    @Override
    public Mono<Transaction> findByIdWithCostumer(String id) {
        return transactionRepository.findById(id).map( x -> {
            x.setCustomer(customerClient.getCustomer(x.getCustomerId()).block());
            return x;
        });
    }
}
