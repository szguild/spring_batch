package com.customer.batch.vo;

import lombok.Data;

@Data
public class Customer {
    private Long customerId;
    private String name;
    private String birthday;
    private String nationality;
}