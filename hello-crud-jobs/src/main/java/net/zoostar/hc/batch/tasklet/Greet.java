package net.zoostar.hc.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Greet implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("{}", "Hello CRUD Batch Job");
		log.info("Step Contribution: {}", contribution);
		log.info("Chunk Context: {}", chunkContext);
		return RepeatStatus.FINISHED;
	}

}
