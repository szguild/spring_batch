package com.customer.model;

import lombok.Builder;
import lombok.Data;


@Data
public class Customer {
	private long rollNum;
	private String stdName;
	private int subjectAMark;
	private int subjectBMark;
}
