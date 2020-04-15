package com.customer.batch.job;

import javax.sql.DataSource;

import com.customer.batch.processor.CustomerProcessor;
import com.customer.batch.vo.Customer;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class JobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

	@Bean
	public LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<Customer>();
		lineMapper.setLineTokenizer(new DelimitedLineTokenizer() {
			{
                setNames(new String[] { "transactionId", "transactionDate", "customerId", "amount" });
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
		  .resource(new ClassPathResource("정보시스템 데이터 개발자_샘플데이터_customer.csv")) //csv 경로 지정 후 조정
		  .lineMapper(lineMapper())
		  .linesToSkip(1)
		  .build();
	}

	@Bean
	public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
	    return new JdbcBatchItemWriterBuilder<Customer>()
		  .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>())
		  .sql("INSERT INTO CUSTOMER (CUSTOMER_ID, NAME, BIRTHDAY, NATIONALITY) VALUES (:customerId, :name,:birthday,:nationality)")
		  .dataSource(dataSource)
		  .build();
	}

	@Bean
	public ItemProcessor<Customer, Customer> processor() {
	    return new CustomerProcessor();
	}

	@Bean
	public Job createCustomerJob(JobCompletionListener listener, Step step1) {
	    return jobBuilderFactory
		  .get("createCustomerJob")
		  .incrementer(new RunIdIncrementer())
		  .listener(listener)
		  .flow(step1)
		  .end()
		  .build();
	}

	@Bean
	public Step step1(ItemReader<Customer> reader, ItemWriter<Customer> writer,
			ItemProcessor<Customer, Customer> processor) {
	    return stepBuilderFactory
		  .get("step1")
		  .<Customer, Customer>chunk(5)
		  .reader(reader)
		  .processor(processor)
		  .writer(writer)
		  .build();
	}

	@Bean
	public DataSource getDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/world");
		dataSource.setUsername("root");
		dataSource.setPassword("P@ssw0rd1");
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}

	//@Override
	public void setDataSource(DataSource dataSource) {
	}

}