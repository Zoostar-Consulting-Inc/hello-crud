package net.zoostar.hc.batch.tasklet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.MdmProduct;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.ProductService;

@Slf4j
@ActiveProfiles({"test", "hsql-test"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
//@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ContextConfiguration(locations = {"classpath:META-INF/job-product-snapshot.xml"})
class LoadProductTest {
	
	private static final String SOURCE = "MDM";
	
	private SecureRandom random = new SecureRandom();
	
	@Autowired
	JobRepository jobRepository;
	
	@Autowired
	Job job;
	
	@Autowired
	ProductService productManager;
	
	@BeforeEach
	protected final void beforeEach(TestInfo test) throws Exception {
		System.out.println();
		log.info("Executing test: [{}]...", test.getDisplayName());
	}
	
	@Test
	void testExecuteJobLoadProduct() throws Exception {
		// GIVEN
		JobParameters jobParameters = new JobParametersBuilder().
				addLong("random", random.nextLong()).
				addString("source", SOURCE).
				addLong("readerPageSize", 3L).
				addString("readerSelectClause", "id AS ID, psku AS PROD_SKU, name AS PROD_NAME, description as PROD_DESC").
				addString("readerFromClause", "product").
				addString("readerSortKey", "ID").
				addString("mappedClass", MdmProduct.class.getName()).
				toJobParameters();
		
		// WHEN
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		launcher.afterPropertiesSet();
		JobExecution jobExecution = launcher.run(job, jobParameters);
		
		// THEN
		assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		Page<Product> page = productManager.retrieve(0, 100);
		assertEquals(11, page.getNumberOfElements());
		Product product = productManager.retrieveBySku("SKU1");
		assertTrue(product.getId().length() > 0);
		assertEquals("One", product.getName());
		assertEquals("Description One", product.getDesc());
		assertEquals(SOURCE, product.getSource());
	}

}
