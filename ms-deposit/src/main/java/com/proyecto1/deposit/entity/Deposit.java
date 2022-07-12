package com.proyecto1.deposit.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "schema_people.deposits")
@Data
public class Deposit {

    @Id
    private String id;
    private Date date;
    private BigDecimal depositAmount;
    private String description;
}
