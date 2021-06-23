package net.zoostar.hc.batch.tasklet;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles({"test", "hsql-test"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/datasource-job.xml", "classpath:META-INF/job-greeting.xml"})
class GreetTest {
	
	private SecureRandom random = new SecureRandom();
	
	@Autowired
	JobRepository jobRepository;
	
	@Autowired
	Job job;

	@Test
	void testExecuteJobOne() throws Exception {
		// GIVEN
		JobParameters jobParameters = new JobParametersBuilder().
				addLong("random", random.nextLong()).
				toJobParameters();
		
		// WHEN
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		launcher.afterPropertiesSet();
		JobExecution jobExecution = launcher.run(job, jobParameters);
		
		// THEN
		assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}

}
