package com.customer.batch.vo;

import java.util.Date;

import lombok.Data;

@Data
public class Transaction {
    private Long transactionId;
    private Date transactionDate;
    private Long customerId;
    private Long amount;
}