package com.customer.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.customer.model.Marksheet;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {
	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Executing job id " + jobExecution.getId());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
	        List<Marksheet> result = jdbcTemplate.query("SELECT rollNum, studentName, totalMarks FROM marksheet", 
	        		new RowMapper<Marksheet>() {
	            @Override
	            public Marksheet mapRow(ResultSet rs, int row) throws SQLException {
	                return new Marksheet(rs.getLong(1), rs.getString(2), rs.getInt(3));
	            }
	        });
	        System.out.println("Number of Records:"+result.size());
		}
	}
}