package com.proyecto1.transaction.service.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1.transaction.client.CustomerClient;
import com.proyecto1.transaction.client.ProductClient;
import com.proyecto1.transaction.entity.Customer;
import com.proyecto1.transaction.entity.Product;
import com.proyecto1.transaction.entity.Transaction;
import com.proyecto1.transaction.repository.TransactionRepository;
import com.proyecto1.transaction.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransacionServiceImpl implements TransactionService {

    private static final Logger log = LogManager.getLogger(TransacionServiceImpl.class);
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CustomerClient customerClient;
    
    @Autowired
    ProductClient product;

    @Override
    public Flux<Transaction> findAll() {
        log.info("Method call FindAll - transaction");
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> save(Transaction t) {
        log.info("Method call Create - transaction");
        //productsPerCustomerValidation(t).subscribe(e -> log.info(e));
        
        long accountPersonalCustomer = this.findAllWithDetail()
                .filter( x -> x.getCustomerId().equals(t.getCustomerId()))
                .filter( x -> (x.getProduct().getIndProduct() == 2 && x.getCustomer().getTypeCustomer() == 1) )
                .count().share().block();
        
        if (accountPersonalCustomer > 0) {
            throw new RuntimeException("El cliente personal no puede tener mas de una cuenta bancaria");
        }
        
        long creditPersonalCustomer = this.findAllWithDetail()
                .filter( x -> x.getCustomerId().equals(t.getCustomerId()))
                .filter( x -> (x.getProduct().getIndProduct() == 1 && x.getCustomer().getTypeCustomer() == 1) )
                .count().share().block();
        
        if (creditPersonalCustomer > 0) {
            throw new RuntimeException("El cliente personal no puede tener mas de un credito");
        }
        
        // Datos del producto a crear
        Product productoToCreate = product.getProduct(t.getProductId())
                .filter( y -> (y.getIndProduct() == 2) ) // Validamos si el producto es PASIVO
                .filter( y -> (y.getTypeProduct() == 1 || y.getTypeProduct() == 3) ) // Validar si es cuenta de ahorros o plazo fijo
                .share().block();
        
        // Datos del cliente solicitante el producto
        Customer customerToTransaction = customerClient.getCustomer(t.getCustomerId())
                .filter( (z -> z.getTypeCustomer() == 2) ) // Validar si el customerId es empresarial
                .share().block();
        
        if (productoToCreate != null && customerToTransaction != null) {
            throw new RuntimeException("El cliente empresarial no puede tener una cuenta de ahorros o plazo fijo");
        }
        
        return Mono.just(t).filterWhen(trans -> limitsAndCommissionValidation(trans))
        		.flatMap(tFiltered -> {
        			if(tFiltered != null) {
        				return transactionRepository.save(t);
        			} else {
        				log.warn("No se registro la cuenta");
        				throw new RuntimeException("validacion limite movimientos mensuales y comision");
        			}
        			
        		});
        /*
        return Mono.just(t).filterWhen(trans -> limitsAndCommissionValidation(trans))
        		//.filterWhen(trans2 -> productsPerCustomerValidation(trans2))
        		.flatMap(tFiltered -> {
        			if(tFiltered != null) {
        				return transactionRepository.save(t);
        			} else {
        				log.warn("No se registro la cuenta");
        				return Mono.just(new Transaction());
        			}
        			
        		});
        /*
        Flux<Properties> properties = Mono.just(productId)
        		.filterWhen(prodId -> productService.isProductNotExcluded(prodId))
        		.map(validProductId -> propertiesService.getProductDetailProperties(validProductId));
        
        /*
        return productsPerCustomerValidation(t).flatMap(b1 -> {
        		limitsAndCommissionValidation(t).flatMap(b2 -> {
        			if(b1 && b2) {
        				return transactionRepository.save(t);
        			}else {
        				log.warn("No se registro la cuenta");
        				return Mono.just(new Transaction());
        			}
        		});
        		log.warn("No se registro la cuenta");
				return Mono.just(new Transaction()); 
        	});
        
        /*
    	return limitsAndCommissionValidation(t).flatMap(b -> {
    		return Mono.just(t).flatMap(trans -> {
    			if(b==true) {
    				return productsPerCustomerValidation(t).flatMap(ppcv -> {
    					if (ppcv == true) {
    						return transactionRepository.save(t);
        				} else {
        					log.warn("No se registro la cuenta");
            				return Mono.just(new Transaction());
        				}
    				});
    			}else {
    				log.warn("No se registro la cuenta");
    				return Mono.just(new Transaction());
    			}
    			
    		});
    	});*/
    }

    @Override
    public Mono<Transaction> findById(String id) {
        log.info("Method call FindById - transaction");
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> update(Transaction t, String id) {
        log.info("Method call Update - transaction");
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
        log.info("Method call Delete - transaction");
        return transactionRepository.findById(id).flatMap( x -> transactionRepository.delete(x).then(Mono.just(new Transaction())));
    }

    @Override
    public Mono<Transaction> findByIdWithCustomer(String id) {
        log.info("Method call FindByIdWithCustomer - transaction");
        return transactionRepository.findById(id).map( x -> {
            x.setCustomer(customerClient.getCustomer(x.getCustomerId()).block());
            x.setProduct(product.getProduct(x.getProductId()).block());
            return x;
        });
    }
    
    public Mono<Boolean> limitsAndCommissionValidation(Transaction t) {
    	// Ahorro 10 movimientos maximo mensuales
    	// Cuenta corriente sin limite movimientos
    	
    	return product.getProduct(t.getProductId()).flatMap(product -> {
    			// Ahorro 1
    			if(product.getTypeProduct() == 1) {
    				BigDecimal i = new BigDecimal(0.0);
    				if(t.getMovementLimit() <= 10 && t.getMaintenanceCommission().equals(i)) {
    					return Mono.just(true);
    				} else {
    					log.warn("Limite 10 movimiento mensual sin comision");
    					return Mono.just(false);
    				}
    			}
    			// Cuenta Corriente 2
    			if(product.getTypeProduct() == 2) {
    				return Mono.just(true);
    			}
    			// Plazo Fijo 3
    			if(product.getTypeProduct() == 3) {
    				if(t.getMovementLimit() <= 1 && t.getMaintenanceCommission().equals(new BigDecimal(0))) {
    					return Mono.just(true);
    				} else {
    					log.warn("Limite 1 movimiento mensual sin comision");
    					return Mono.just(false);
    				}
    			}
    			log.warn("No se encontro el tipo de producto");
    			return Mono.just(false);
    		});
    }
    
    public Mono<Boolean> productsPerCustomerValidation(Transaction t) {
    	
    	//Long count = this.findByIdWithCustomer(t.getId()).filter(f1 -> f1.get);
    	
    	return Mono.just(null);
    	
    	/*
    	
    	return product.getProduct(t.getProductId()).flatMap(product -> {
    			
    			if(customerClient.getCustomer(t.getCustomerId()).block().getTypeCustomer() == 1) {
    				// Tipo cliente personal
            		if(product.getTypeProduct() == 4) {
            			// Producto-Credito-Personal
    					return (transactionRepository.findAll().filter(cuenta -> cuenta.getCustomerId()
    							.equalsIgnoreCase(t.getCustomerId()))
    							.hasElements() == Mono.just(true)) ? Mono.just(false):Mono.just(true);
            		}
            		
            		if(product.getTypeProduct() == 1) {
            			// Producto-Cuenta-de-ahorro
            			log.warn("Ya tiene una cuenta de ahorro");
    					return Mono.just(false);
            		}
            		
            		if(product.getTypeProduct() == 2) {
            			// Producto-Cuenta-corriente
            			log.warn("Ya tiene una cuenta corriente");
    					return Mono.just(false);
            		}
            		
            		
    			} else if(customerClient.getCustomer(t.getCustomerId()).block().getTypeCustomer() == 2){
    				// Tipo de cliente empresarial
    				if(product.getTypeProduct() == 5) {
            			// Producto-Credito-Empresarial
    					return Mono.just(true);
            		}
    				
   
    			}
    				
    			// Producto-Credito-Personal
        		if(product.getTypeProduct() == 4) {
    				
    				BigDecimal i = new BigDecimal(0.0);
    				if(t.getMovementLimit() <= 10 && t.getMaintenanceCommission().equals(i)) {
    					return Mono.just(true);
    				} else {
    					log.warn("Limite 10 movimiento mensual sin comision");
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
    					log.warn("Limite 1 movimiento mensual sin comision");
    					return Mono.just(false);
    				}
    			}
    			log.warn("No se encontro el tipo de producto");
    			return Mono.just(false);
    		});*/
    }

	@Override
	public Flux<Transaction> findAllWithDetail() {
		
		return this.findAll().map(transactions -> {
			transactions.setCustomer(customerClient.getCustomer(transactions.getCustomerId()).block());
			transactions.setProduct(product.getProduct(transactions.getProductId()).block());
			return transactions;
		});
	}
}
