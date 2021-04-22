package com.test.spring.batch;

import org.apache.camel.ProducerTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.test.spring.batch.cloud.aws.s3.SpringCloudS3;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SpringCloudS3 springCloudS3;

	@Autowired
	private ProducerTemplate producerTemplate;
	

	@Bean
	public Tasklet aggregateFilesTask() {
		Tasklet tasklet = new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Starting tasklet to aggregate files...");
				
				producerTemplate.getCamelContext().startAllRoutes();
				producerTemplate.sendBody("direct:start","[]");
				
				return RepeatStatus.FINISHED;
			}
		};
		return tasklet;
	}

	@Bean
	public Job fileAggregatorJob(Step step1) {
		return jobBuilderFactory
				.get("fileAggregatorJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory
				.get("Aggregate-files-step")
				.tasklet(aggregateFilesTask())
				.build();
	}

}