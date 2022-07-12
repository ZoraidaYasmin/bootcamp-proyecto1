package com.proyecto1.withdrawal.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "schema_people.withdrawals")
@Data
public class Withdrawal {

    @Id
    private String id;
    private Date date;
    private BigDecimal withdrawalAmount;
    private String description;
}
