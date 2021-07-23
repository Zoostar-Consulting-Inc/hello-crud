package net.zoostar.hc.web.controller.api;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.web.request.RequestBulkInsert;
import net.zoostar.hc.web.request.RequestUser;

@Getter
@RestController
@RequestMapping(path = "/api/user")
public class UserRestController extends AbstractCrudController<User> implements ApplicationContextAware {
	
	protected ApplicationContext applicationContext;
	
	@Autowired
	protected JobRepository jobRepository;
    
    private SecureRandom random = new SecureRandom();
    
//	@Autowired
//	Job job;
	
	@Setter
	@Autowired
	protected UserService crudManager;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody RequestUser request) {
		return super.create(request);
	}
	
	@Override
	@GetMapping(value = "/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<User>> retrieve(@PathVariable int number, @RequestParam int limit) {
		return super.retrieve(number, limit);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> update(@RequestBody RequestUser request) {
		return super.update(request);
	}
	
	@Override
	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> deleteByKey(@RequestBody Map<String, String> request) {
		return super.deleteByKey(request);
	}

	@Override
	@PostMapping(value = "/retrieve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> retrieveByKey(@RequestBody Map<String, String> request) {
		return super.retrieveByKey(request);
	}

    @PostMapping("/bulk/insert")
    public ResponseEntity<BatchStatus> bulkInsert(@RequestBody RequestBulkInsert<User> request) throws Exception {
    	log.info("Bulk insert using request: {}", request.toString());
    	SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		launcher.afterPropertiesSet();
		log.info("Job launcher initialized: {}.", launcher);

		Job userJob = applicationContext.getBean("bulk-insert-users", Job.class);
    	log.info("Launching job: {}...", userJob.toString());
		JobParameters jobParameters = new JobParametersBuilder().
				addString("source", request.getSource()).
				addLong("readerPageSize", request.getReaderPageSize()).
				addString("readerSelectClause", request.getReaderSelectClause()).
				addString("readerFromClause", request.getReaderFromClause()).
				addString("readerSortKey", request.getReaderSortKey()).
				addString("mappedClass", request.getMappedClass()).
				toJobParameters();
		JobExecution execution = launcher.run(userJob, jobParameters);
		log.info("Job Execution Status: {}", execution);
		return new ResponseEntity<>(execution.getStatus(), HttpStatus.OK);
    }

	@Override
	protected User retrieveEntity(Map<String, String> request) throws MissingEntityException {
		return getCrudManager().retrieveByEmail(request.get("email"));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
