package com.customer.processor;

import com.customer.model.Customer;
import com.customer.model.Marksheet;

import org.springframework.batch.item.ItemProcessor;

public class CustomerItemProcessor implements ItemProcessor<Customer, Marksheet> {
	@Override
	public Marksheet process(final Customer customer) throws Exception {
		int totalMarks = customer.getSubjectAMark() + customer.getSubjectBMark();
		System.out.println("Student name:" + customer.getStdName() + " and Total marks:" + totalMarks);
		Marksheet marksheet = new Marksheet(customer.getRollNum(), customer.getStdName(), totalMarks);
		return marksheet;
	}
}
