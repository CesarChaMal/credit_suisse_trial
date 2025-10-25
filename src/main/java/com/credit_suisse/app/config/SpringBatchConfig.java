package com.credit_suisse.app.config;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
public class SpringBatchConfig {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new AbstractPlatformTransactionManager() {
            @Override
            protected Object doGetTransaction() {
                return new Object();
            }
            
            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
                // No-op for in-memory processing
            }
            
            @Override
            protected void doCommit(DefaultTransactionStatus status) {
                // No-op for in-memory processing
            }
            
            @Override
            protected void doRollback(DefaultTransactionStatus status) {
                // No-op for in-memory processing
            }
        };
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource());
        factory.setTransactionManager(transactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    
    @Bean
    public DataSource batchDataSource() {
        String dbName = "configdb_" + System.currentTimeMillis();
        return new EmbeddedDatabaseBuilder()
            .setName(dbName)
            .setType(EmbeddedDatabaseType.H2)
            .addScript("org/springframework/batch/core/schema-h2.sql")
            .build();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        return new org.springframework.batch.core.launch.support.TaskExecutorJobLauncher() {
            {
                setJobRepository(jobRepository);
                setTaskExecutor(taskExecutor());
            }
        };
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}