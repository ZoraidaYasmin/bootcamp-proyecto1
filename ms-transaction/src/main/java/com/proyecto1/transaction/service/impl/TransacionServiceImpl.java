package com.proyecto1.transaction.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1.transaction.client.CustomerClient;
import com.proyecto1.transaction.entity.Transaction;
import com.proyecto1.transaction.repository.TransactionRepository;
import com.proyecto1.transaction.service.TransactionService;
import com.proyecto1.transaction.signatory.ProductWebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransacionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CustomerClient customerClient;
    
    @Autowired
    ProductWebClient product;

    @Override
    public Flux<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> save(Transaction t) {
    	
    	return limitsAndCommissionValidation(t).flatMap(b -> {
    		return Mono.just(t).flatMap(trans -> {
    			if(b==true) {
    				return transactionRepository.save(t);
    			}else {
    				return Mono.just(new Transaction());
    			}
    			
    		});
    	});
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
    
    public Mono<Boolean> limitsAndCommissionValidation(Transaction t) {
    	// Ahorro 10 movimientos maximo mensuales
    	// Cuenta corriente sin limite movimientos
    	
    	return product.getProduct(t.getProductId()).flatMap(product -> {
    			// Ahorro 1
    			if(product.getTypeProduct() == 1) {
    				if(t.getMovementLimit() <= 10 && t.getMaintenanceCommission() == new BigDecimal(0)) {
    					return Mono.just(true);
    				} else {
    					return Mono.just(false);
    				}
    			}
    			// Cuenta Corriente 2
    			if(product.getTypeProduct() == 2) {
    				return Mono.just(true);
    			}
    			// Plazo Fijo 3
    			if(product.getTypeProduct() == 3) {
    				if(t.getMovementLimit() <= 1 && t.getMaintenanceCommission() == new BigDecimal(0)) {
    					return Mono.just(true);
    				} else {
    					return Mono.just(false);
    				}
    			}
    			
    			return Mono.just(false);
    		});
    }
}
