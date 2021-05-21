package net.zoostar.hc.web.controller.api;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.web.filter.GatewayAuditFilterChain;

@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
public abstract class AbstractApiRestController<T extends AbstractStringPersistable> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected final ObjectMapper mapper = new ObjectMapper();
	
	protected List<T> entities;

	@Autowired
	WebApplicationContext wac;
	
	MockMvc mockMvc;
	
	public void beforeEach(TestInfo test) throws JsonParseException, JsonMappingException, IOException {
		System.out.println();
		log.info("Executing test: [{}]...", test.getDisplayName());
		
		entities = entities(path(), classType());
		var filter = new GatewayAuditFilterChain();
		Assert.assertNull(GatewayAuditFilterChain.GATEWAY_AUDIT_HOLDER.get());
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).
				addFilters(filter).build();
	}

	protected List<T> entities2(String path, Class<T> clazz) {
		try {
			return Collections.unmodifiableList(mapper.readValue(
					new ClassPathResource(path).getInputStream(),
					new TypeReference<List<T>>() { }));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	protected List<T> entities(String path, Class<T> clazz) throws 
	JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().
				constructCollectionType(List.class, clazz);
		return Collections.unmodifiableList(mapper.readValue(
				new ClassPathResource(path).getInputStream(),
				type));
	}

	protected Page<T> page(List<T> entities, PageRequest page) {
		int size = entities.size();
		int limit = page.getPageSize();
		int from = page.getPageNumber() * limit;
		int to = from + limit;
		if(to > size) {
			to = from + (size % from);
		}
		log.info("Given page number {} and page size {}...", (page.getPageNumber() + 1), limit);
		log.info("Then return a total of {}/{} entities from {} to {}.", (to - from), size, (from + 1), to);
		return new PageImpl<>(entities.subList(from, to), page, entities.size());
	}

	protected abstract Class<T> classType();
	protected abstract String path();
	protected abstract String url();
}
