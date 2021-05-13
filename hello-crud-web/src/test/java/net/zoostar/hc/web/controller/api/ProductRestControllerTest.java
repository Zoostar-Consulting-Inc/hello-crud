package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.impl.ProductServiceImpl;
import net.zoostar.hc.web.filter.GatewayAuditFilterChain;
import net.zoostar.hc.web.request.RequestProduct;

@Slf4j
@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
class ProductRestControllerTest {
	
	protected ObjectMapper mapper = new ObjectMapper();
	
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
	void testCreateSuccess() throws Exception {
		var url = new StringBuilder("/secured/api/product").toString();
		var page = page(entities, PageRequest.of(0, 1));
		var entity = page.getContent().get(0);
		var id = entity.getId();
		
		//GIVEN
		var request = new RequestProduct();
		request.setSku(entity.getSku());
		request.setName(entity.getName());
		request.setDesc(entity.getDesc());

		//WHEN
		when(controller.getProductManager().getRepository().
				save(request.toEntity())).thenReturn(entity);
		
		//THEN
		String value = mapper.writeValueAsString(request);
		log.info("Perform POST for URL: {}", url);
		log.info("Sending create request: {}", value);
		var result = mockMvc.perform(post(url).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(value).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
	    		andExpect(status().isCreated()).
	    		andExpect(content().contentType(MediaType.APPLICATION_JSON)).
	    		andExpect(content().string(mapper.writeValueAsString(entity))).
	    		andReturn();
		assertNotNull(result);
		
		var response = result.getResponse();
		assertNotNull(response);
		log.info("Response: {}", response.getContentAsString());
		entity = mapper.readValue(response.getContentAsString(), Product.class);
		assertNotNull(entity);
		assertEquals(id, entity.getId());
		assertEquals(request.getName(), entity.getName());
		assertEquals(request.getName(), entity.getName());
		assertEquals(request.getDesc(), entity.getDesc());
		assertNotEquals(entity, null);
		assertNotEquals(entity, request);
	}

	@Test
	void testCreateFailureMissingRequiredFieldException() throws Exception {
		var url = new StringBuilder("/secured/api/product").toString();
		var page = page(entities, PageRequest.of(0, 1));
		var entity = page.getContent().get(0);
		
		//GIVEN
		var request = new RequestProduct();
		request.setName(entity.getName());
		request.setDesc(entity.getDesc());

		//THEN
		String value = mapper.writeValueAsString(request);
		log.info("Perform POST for URL: {}", url);
		log.info("Sending create request: {}", value);
		mockMvc.perform(post(url).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(value).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
	    		andExpect(status().isExpectationFailed()).
	    		andExpect(content().contentType(MediaType.APPLICATION_JSON)).
	    		andExpect(content().string(mapper.writeValueAsString(request.toEntity())));
	}
	
	@Test
	void testRetrieveByPageOneOfMany() throws Exception {
		int number = 0;
		int limit = 3;
		var url = new StringBuilder("/secured/api/product/").
				append(number).append("?limit=").append(limit).toString();
		
		var request = PageRequest.of(number, limit);
		var page = page(entities, request);
		when(controller.getProductManager().getRepository().findAll(request)).
				thenReturn(page);

		log.info("Perform GET for URL: {}", url);
	    var result = mockMvc.perform(get(url).
			accept(MediaType.APPLICATION_JSON_VALUE)).
			andExpect(status().isOk()).
			andExpect(content().contentType(MediaType.APPLICATION_JSON)).
			andExpect(content().string(mapper.writeValueAsString(page))).
			andReturn();
	    assertNotNull(result);

	    var response = result.getResponse();
		assertNotNull(response);
		log.info("Response: {}", response.getContentAsString());
	    
		assertEquals(Integer.valueOf(entities.size()).longValue(), page.getTotalElements());
		assertEquals(limit, page.getNumberOfElements());
		assertEquals(number, page.getNumber());
		assertTrue(page.hasNext());
		assertFalse(page.hasPrevious());
		
		List<Product> products = page.getContent();
		for(int i=0; i<products.size(); i++) {
			var product = products.get(i);
			var entity = entities.get(i);
			assertEquals(product, entity);
			assertEquals(product.hashCode(), entity.hashCode());
		}
	}
	
	@Test
	void testRetrieveByPageNextOfMany() throws Exception {
		int number = 1;
		int limit = 3;
		var url = new StringBuilder("/secured/api/product/").
				append(number).append("?limit=").append(limit).toString();
		
		var request = PageRequest.of(number, limit);
		var page = page(entities, request);
		when(controller.getProductManager().getRepository().findAll(request)).
				thenReturn(page);

		log.info("Perform GET for URL: {}", url);
	    var result = mockMvc.perform(get(url).
			accept(MediaType.APPLICATION_JSON_VALUE)).
			andExpect(status().isOk()).
			andExpect(content().contentType(MediaType.APPLICATION_JSON)).
			andExpect(content().string(mapper.writeValueAsString(page))).
			andReturn();
	    assertNotNull(result);

	    var response = result.getResponse();
		assertNotNull(response);
		log.info("Response: {}", response.getContentAsString());
	    
		assertEquals(Integer.valueOf(entities.size()).longValue(), page.getTotalElements());
		assertEquals(limit, page.getNumberOfElements());
		assertEquals(number, page.getNumber());
		assertTrue(page.hasNext());
		assertTrue(page.hasPrevious());
		
		List<Product> products = page.getContent();
		for(int i=0; i<products.size(); i++) {
			var product = products.get(i);
			var entity = entities.get((number * limit) + i);
			assertEquals(product, entity);
			assertEquals(product.hashCode(), entity.hashCode());
		}
	}
	
	@Test
	void testRetrieveByPageLastOfMany() throws Exception {
		int number = 3;
		int limit = 3;
		var url = new StringBuilder("/secured/api/product/").
				append(number).append("?limit=").append(limit).toString();
		
		var request = PageRequest.of(number, limit);
		var page = page(entities, request);
		when(controller.getProductManager().getRepository().findAll(request)).
				thenReturn(page);

		log.info("Perform GET for URL: {}", url);
	    var result = mockMvc.perform(get(url).
			accept(MediaType.APPLICATION_JSON_VALUE)).
			andExpect(status().isOk()).
			andExpect(content().contentType(MediaType.APPLICATION_JSON)).
			andExpect(content().string(mapper.writeValueAsString(page))).
			andReturn();
	    assertNotNull(result);

	    var response = result.getResponse();
		assertNotNull(response);
		log.info("Response: {}", response.getContentAsString());
	    
		assertEquals(Integer.valueOf(entities.size()).longValue(), page.getTotalElements());
		assertEquals(limit-1, page.getNumberOfElements());
		assertEquals(number, page.getNumber());
		assertFalse(page.hasNext());
		assertTrue(page.hasPrevious());
		
		List<Product> products = page.getContent();
		for(int i=0; i<products.size(); i++) {
			var product = products.get(i);
			var entity = entities.get((number * limit) + i);
			assertEquals(product, entity);
			assertEquals(product.hashCode(), entity.hashCode());
		}
	}
	
	protected List<Product> entities(String path) throws JsonParseException, JsonMappingException, IOException {
		return Collections.unmodifiableList(mapper.readValue(
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
