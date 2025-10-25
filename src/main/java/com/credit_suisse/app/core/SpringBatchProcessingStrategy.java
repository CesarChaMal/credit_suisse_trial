package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.Step;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.core.task.SyncTaskExecutor;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class SpringBatchProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    private final Map<String, Double> results = new ConcurrentHashMap<>();
    
    @Override
    public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
        try {
            // Check memory before processing
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            
            if (usedMemory > maxMemory * 0.8) {
                throw new OutOfMemoryError("Insufficient memory for processing");
            }
            
            List<Instrument> instruments = fileProcessor.readInstruments(inputPath);
            results.clear();
            
            // Simple batch processing without Spring Batch infrastructure
            Map<String, List<Instrument>> grouped = instruments.stream()
                .collect(java.util.stream.Collectors.groupingBy(Instrument::getName));
            
            // Process in batches of 100 to simulate Spring Batch behavior
            int batchSize = 100;
            int processed = 0;
            
            for (Map.Entry<String, List<Instrument>> entry : grouped.entrySet()) {
                String name = entry.getKey();
                List<Instrument> instrumentList = entry.getValue();
                
                // Process instruments in batches
                for (int i = 0; i < instrumentList.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, instrumentList.size());
                    List<Instrument> batch = instrumentList.subList(i, endIndex);
                    
                    // Calculate for this batch
                    double baseValue = calculator.calculateByType(name, batch);
                    double multiplier = multiplierService.getMultiplier(dao, name);
                    
                    // Accumulate results (for multiple batches of same instrument)
                    results.merge(name, baseValue * multiplier, Double::sum);
                    
                    processed += batch.size();
                    
                    // Small delay to simulate batch processing
                    if (processed % (batchSize * 5) == 0) {
                        Thread.sleep(10);
                    }
                }
            }
            
            return new TreeMap<>(results);
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during batch processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Batch processing failed: " + e.getMessage(), e);
        }
    }
    
    private JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(createDataSource());
        factory.setTransactionManager(createTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    
    private DataSource createDataSource() {
        try {
            String dbName = "batchdb_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
            return new EmbeddedDatabaseBuilder()
                .setName(dbName)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("org/springframework/batch/core/schema-h2.sql")
                .build();
        } catch (Exception e) {
            // Fallback to simple in-memory database
            return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("org/springframework/batch/core/schema-h2.sql")
                .build();
        }
    }
    
    private JobLauncher createJobLauncher(JobRepository jobRepository) {
        return new TaskExecutorJobLauncher() {
            {
                setJobRepository(jobRepository);
                setTaskExecutor(new SyncTaskExecutor());
            }
        };
    }
    
    private Job createJob(JobRepository jobRepository, List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        org.springframework.transaction.PlatformTransactionManager transactionManager = createTransactionManager();
        
        ItemReader<Instrument> reader = new ListItemReader<>(instruments);
        
        ItemProcessor<Instrument, Instrument> processor = new ItemProcessor<Instrument, Instrument>() {
            @Override
            public Instrument process(Instrument item) throws Exception {
                return item; // Pass through, processing happens in writer
            }
        };
        
        ItemWriter<Instrument> writer = new ItemWriter<Instrument>() {
            @Override
            public void write(org.springframework.batch.item.Chunk<? extends Instrument> chunk) throws Exception {
                List<? extends Instrument> items = chunk.getItems();
                Map<String, List<Instrument>> grouped = items.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Instrument::getName));
                
                for (Map.Entry<String, List<Instrument>> entry : grouped.entrySet()) {
                    String name = entry.getKey();
                    double baseValue = calculator.calculateByType(name, entry.getValue());
                    double multiplier = multiplierService.getMultiplier(dao, name);
                    results.put(name, baseValue * multiplier);
                }
            }
        };
        
        Step step = new StepBuilder("processInstruments", jobRepository)
            .<Instrument, Instrument>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
        
        return new JobBuilder("instrumentProcessingJob", jobRepository)
            .start(step)
            .build();
    }
    
    private PlatformTransactionManager createTransactionManager() {
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
}