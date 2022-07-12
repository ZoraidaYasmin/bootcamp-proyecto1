package com.proyecto1.payment.service.impl;

import com.proyecto1.payment.entity.Payment;
import com.proyecto1.payment.repository.PaymentRepository;
import com.proyecto1.payment.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public Flux<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Mono<Payment> create(Payment c) {
        return paymentRepository.save(c);
    }

    @Override
    public Mono<Payment> findById(String id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Mono<Payment> update(Payment c, String id) {
        return paymentRepository.findById(id)
                .map( x -> {
                    x.setDate(c.getDate());
                    x.setPaymentAmount(c.getPaymentAmount());
                    x.setDescription(c.getDescription());
                    return x;
                }).flatMap(paymentRepository::save);
    }

    @Override
    public Mono<Payment> delete(String id) {
        return paymentRepository.findById(id).flatMap( x -> paymentRepository.delete(x).then(Mono.just(new Payment())));
    }
}
