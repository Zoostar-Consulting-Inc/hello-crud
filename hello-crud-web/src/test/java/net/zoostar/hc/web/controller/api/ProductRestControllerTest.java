package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.Content;
import net.zoostar.hc.web.filter.GatewayAuditFilterChain;
import net.zoostar.hw.dao.hsql.ProductRepository;
import net.zoostar.hw.model.Product;
import net.zoostar.hw.service.impl.ProductServiceImpl;

@Slf4j
@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
class ProductRestControllerTest {
	
	protected List<Product> entities;

	@Autowired
	WebApplicationContext wac;
	
	MockMvc mockMvc;

	@Mock
	protected ProductRepository repository;

	@InjectMocks
	protected ProductServiceImpl productManager;
	
	@Autowired
	protected ProductRestController controller;
	
	@BeforeEach
	public void beforeEach(TestInfo test) throws JsonParseException, JsonMappingException, IOException {
		System.out.println();
		log.info("Executing test: [{}]...", test.getDisplayName());
		
		GatewayAuditFilterChain filter = new GatewayAuditFilterChain();
		Assert.assertNull(GatewayAuditFilterChain.GATEWAY_AUDIT_HOLDER.get());
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).
				addFilters(filter).build();
		
		entities = entities("data/products.json");
		log.info("Total number of entities loaded: {}", entities.size());
		
		controller.setProductManager(productManager);
	}

	@Test
	void testRetrieveByPageOneOfMany() throws Exception {
		//GIVEN
		int number = 0;
		int limit = 3;
		var url = new StringBuilder("/secured/api/product/").
				append(number).append("?limit=").append(limit);
		
		//MOCK
		PageRequest page = PageRequest.of(number, limit);
		when(controller.getProductManager().getRepository().findAll(page)).
				thenReturn(page(entities, page));

		//WHEN
		log.info("Perform GET for URL: {}", url.toString());
	    var result = mockMvc.perform(get(url.toString()).accept(MediaType.APPLICATION_JSON)).
	    		andExpect(status().isOk()).andReturn();
	    
		//THEN
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		log.info("Received result: {}", result);

		var response = response(result.getResponse().getContentAsString());
		assertNotNull(response);
		log.info("Received response: {}", response);
		assertEquals(entities.size(), response.getTotalElements());
		assertEquals(limit, response.getNumberOfElements());
		assertEquals(number, response.getNumber());
		assertTrue(response.hasNext());
		assertFalse(response.hasPrevious());
	}
	
	protected Page<Product> response(String response) throws JsonMappingException, JsonProcessingException {
		var content = new ObjectMapper().readValue(response, new TypeReference<Content<Product>>() {});
		return content.getContent();
	}
	
	protected List<Product> entities(String path) throws JsonParseException, JsonMappingException, IOException {
		return Collections.unmodifiableList(new ObjectMapper().readValue(
				new ClassPathResource(path).getInputStream(),
				new TypeReference<List<Product>>() { }));
	}

	protected Page<Product> page(List<Product> entities, PageRequest page) {
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

}
