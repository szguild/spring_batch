package com.customer.configuration;

import javax.sql.DataSource;

import com.customer.listener.JobCompletionListener;
import com.customer.model.Marksheet;
import com.customer.processor.CustomerItemProcessor;
import com.customer.model.Customer;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration extends DefaultBatchConfigurer {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<Customer>();
		lineMapper.setLineTokenizer(new DelimitedLineTokenizer() {
			{
				setNames(new String[] { "rollNum", "stdName", "subjectAMark", "subjectBMark" });
			}
		});
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
			{
				setTargetType(Customer.class);
			}
		});
		return lineMapper;
	}

	@Bean
	public FlatFileItemReader<Customer> reader() {
	    return new FlatFileItemReaderBuilder<Customer>()
		  .name("customerItemReader")		
		  .resource(new ClassPathResource("정보시스템 데이터 개발자_샘플데이터_customer.csv"))
		  .lineMapper(lineMapper())
		  .linesToSkip(1)
		  .build();
	}

	@Bean
	public JdbcBatchItemWriter<Marksheet> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Marksheet>()
		   .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Marksheet>())
		   .sql("INSERT INTO marksheet (rollNum, studentName, totalMarks) VALUES (:rollNum, :stdName,:totalMarks)")
		   .dataSource(dataSource)
		   .build();
	}

	@Bean
	public ItemProcessor<Customer, Marksheet> processor() {
		return new CustomerItemProcessor();
	}

	@Bean
	public Job createMarkSheetJob(JobCompletionListener listener, Step step1) {
		return jobBuilderFactory
		  .get("createMarkSheetJob")
		  .incrementer(new RunIdIncrementer())
		  .listener(listener)
		  .flow(step1)
		  .end()
		  .build();
	}

	@Bean
	public Step step1(ItemReader<Customer> reader, ItemWriter<Marksheet> writer,
			ItemProcessor<Customer, Marksheet> processor) {
		 return stepBuilderFactory
		   .get("step1")
		   .<Customer, Marksheet>chunk(5)
		   .reader(reader)
		   .processor(processor)
		   .writer(writer)
		   .build();
	}

	@Bean
	public DataSource getDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/cpdb");
		dataSource.setUsername("root");
		dataSource.setPassword("cp");
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
	}
}