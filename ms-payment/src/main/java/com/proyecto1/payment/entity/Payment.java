package com.proyecto1.payment.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "schema_people.payments")
@Data
public class Payment {

    @Id
    private String id;
    private Date date;
    private BigDecimal paymentAmount;
    private String description;
}
