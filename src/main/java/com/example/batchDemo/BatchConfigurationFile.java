package com.example.batchDemo;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfigurationFile {
    @Bean
    protected FlatFileItemReader<String> reader() {
        return new FlatFileItemReaderBuilder<String>()
                .resource(new ClassPathResource("data-file.csv"))
                .name("csv-reader")
                .lineMapper((line, lineNumber) -> line)
                .build();
    }

    @Bean
    protected FlatFileItemWriter<String> writer(){
        String fileLocation = "src/main/resources/masked-data.csv";

        return new FlatFileItemWriterBuilder<String>()
                .name("csv-writer")
                .resource(new FileSystemResource(fileLocation))
                .lineAggregator(item -> item) //Lambda to directly return the String
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    protected Step maskingStep(JobRepository jobRepo, PlatformTransactionManager manager,
                               FlatFileItemReader<String> reader, TextItemProcessor processor,
                               FlatFileItemWriter<String> writer) {
        return new StepBuilder("masking-step", jobRepo)
                .<String, String>chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(manager)
                .build();

    }

    @Bean
    protected Job maskingJob(JobRepository jobRepository, Step maskingStep, BatchJobCompletedListener jobCompletedListener){
        return new JobBuilder("masking-job", jobRepository)
                .start(maskingStep)
                .listener(jobCompletedListener)
                .build();
    }
}
