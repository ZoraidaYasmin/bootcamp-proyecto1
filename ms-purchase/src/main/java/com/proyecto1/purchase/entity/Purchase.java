package com.proyecto1.purchase.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "schema_purchase.puchases")
@Data
public class Purchase {

    @Id
    private String id;
    private Date date;
    private BigDecimal purchaseAmount;
    private String description;
}
