package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.web.filter.GatewayAuditFilterChain;
import net.zoostar.hc.web.request.RequestEntity;

@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
public abstract class AbstractCrudControllerTest<T extends AbstractStringPersistable> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected final ObjectMapper mapper = new ObjectMapper();
	
	protected List<T> entities;

	@Autowired
	WebApplicationContext wac;
	
	MockMvc mockMvc;
	
	@BeforeEach
	protected final void beforeEach(TestInfo test) throws Exception {
		System.out.println();
		log.info("Executing test: [{}]...", test.getDisplayName());
		
		entities = entities(path(), getClazz());
		log.info("Loaded {} entities.", entities.size());
		var filter = new GatewayAuditFilterChain();
		Assert.assertNull(GatewayAuditFilterChain.GATEWAY_AUDIT_HOLDER.get());
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).
				addFilters(filter).build();
		additionalSetup();
	}

	protected abstract void additionalSetup() throws Exception;
	
	@Test
	void testCreateSuccess() throws Exception {
		final var expectedEntity = entities.get(0);
		
		//GIVEN
		var request = requestEntity(expectedEntity);

		//MOCK
		when(getService().getCrudManager().getRepository().
				save(request.toEntity())).thenReturn(expectedEntity);

		//WHEN
		log.info("Perform POST for URL: {}", getEndPoint());
		var result = mockMvc.perform(post(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		assertTrue(request.toEntity().isNew());
		var response = result.getResponse();
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		log.info("Response received: {}", response.getContentAsString());
		
		var actualEntity = mapper.readValue(response.getContentAsString(), getClazz());
		log.info("Created new entity: {}", actualEntity);
		assertNotEquals(actualEntity, null);
		assertFalse(expectedEntity.isNew());
		assertEquals(expectedEntity, actualEntity);
		assertEquals(expectedEntity.getClass(), actualEntity.getClass());
		assertEquals(expectedEntity.hashCode(), actualEntity.hashCode());
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		
		additionalSuccessAssertions(expectedEntity, actualEntity);
	}
	
	@Test
	void testCreateFailureNullEntity() throws Exception {
		StringPersistableCrudService<T>  crudManager = getService().getCrudManager();
		
		//GIVEN
		T request = null;
		
		//THEN
		assertThrows(IllegalArgumentException.class, () -> crudManager.create(request));
	}

	@Test
	void testCreateFailureDuplicateEntity() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = requestEntity(entity);

		//MOCK
		whenFindEntity(request.toEntity()).
				thenReturn(Optional.of(entity));

		//WHEN
		log.info("Perform POST for URL: {}", getEndPoint());
		var result = mockMvc.perform(post(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}

	@Test
	void testRetrieveByPageFirstOfMany() throws Exception {
		//GIVEN
		int number = 0;
		int limit = 3;
		
		final var page = testRetrieveByPage(number, limit);
		assertEquals(limit, page.getNumberOfElements());
		assertTrue(page.hasNext());
		assertFalse(page.hasPrevious());
	}
	
	@Test
	void testRetrieveByPageNextOfMany() throws Exception {
		//GIVEN
		int number = 1;
		int limit = 3;
		
		final var page = testRetrieveByPage(number, limit);
		assertEquals(limit, page.getNumberOfElements());
		assertTrue(page.hasNext());
		assertTrue(page.hasPrevious());
	}
	
	@Test
	void testRetrieveByPageLastOfMany() throws Exception {
		//GIVEN
		int number = 3;
		int limit = 3;
		
		final var page = testRetrieveByPage(number, limit);
		assertEquals(limit-1, page.getNumberOfElements());
		assertFalse(page.hasNext());
		assertTrue(page.hasPrevious());
	}
	
	@Test
	void testRetrieveByPageFailureInvalidPageNumber() throws Exception {
		//GIVEN
		int number = -1;
		int limit = 3;

		//WHEN
		var result = mockMvc.perform(get(url(getEndPoint(), number, limit)).
			accept(MediaType.APPLICATION_JSON_VALUE)).
	    	andReturn();

	    //THEN
	    var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}
	
	@Test
	void testRetrieveByPageFailureInvalidLimit() throws Exception {
		//GIVEN
		int number = 0;
		int limit = 0;

		//WHEN
		var result = mockMvc.perform(get(url(getEndPoint(), number, limit)).
			accept(MediaType.APPLICATION_JSON_VALUE)).
	    	andReturn();

	    //THEN
	    var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}
	
	@Test
	void testRetrieveByKeySuccess() throws Exception {
		final var entity = entities.get(0);
		
		//GIVEN
		Map<String, String> request = retrieveByKeyRequest(false, entity);
		
		//MOCK
		whenFindEntity(entity).thenReturn(Optional.of(entity));
		
		//WHEN
		String endPoint = getEndPoint() + "/retrieve";
		log.info("Perform POST for URL: {}", endPoint);
		var result = mockMvc.perform(post(endPoint).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		log.info("Response received: {}", response.getContentAsString());
		
		var actualEntity = mapper.readValue(response.getContentAsString(), getClazz());
		log.info("Retrieved entity: {}", actualEntity);
		assertNotEquals(actualEntity, null);
		assertFalse(actualEntity.isNew());
		assertEquals(entity, actualEntity);
		assertEquals(entity.getClass(), actualEntity.getClass());
		assertEquals(entity.hashCode(), actualEntity.hashCode());
		assertEquals(entity.getId(), actualEntity.getId());
		assertEquals(entity.toString(), actualEntity.toString());
		
		additionalSuccessAssertions(entity, actualEntity);
	}
	
	@Test
	void testRetrieveByKeyFailureNullKey() throws Exception {
		final var entity = entities.get(0);
		
		//GIVEN
		Map<String, String> request = retrieveByKeyRequest(true, entity);
		
		//WHEN
		String endPoint = getEndPoint() + "/retrieve";
		log.info("Perform POST for URL: {}", endPoint);
		var result = mockMvc.perform(post(endPoint).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}
	
	@Test
	void testRetrieveByKeyFailureMissingEntityException() throws Exception {
		final var entity = entities.get(0);
		
		//GIVEN
		Map<String, String> request = retrieveByKeyRequest(false, entity);
		
		//MOCK
		whenFindEntity(entity).thenReturn(Optional.ofNullable(null));
		
		//WHEN
		String endPoint = getEndPoint() + "/retrieve";
		log.info("Perform POST for URL: {}", endPoint);
		var result = mockMvc.perform(post(endPoint).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}

	@Test
	void testUpdateSuccess() throws Exception {
		final var existingEntity = entities.get(0);
		
		//GIVEN
		var request = requestUpdatedEntity(existingEntity);

		//MOCK
		whenFindEntity(request.toEntity()).
				thenReturn(Optional.of(existingEntity));
		
		T updatedEntity = request.toEntity();
		assertTrue(updatedEntity.isNew());
		updatedEntity.setId(existingEntity.getId());
		when(getService().getCrudManager().getRepository().save(request.toEntity())).
				thenReturn(updatedEntity);
		
		//WHEN
		log.info("Perform PUT for URL: {}", getEndPoint());
		var result = mockMvc.perform(put(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		log.info("Response received: {}", response.getContentAsString());
		
		var actualEntity = mapper.readValue(response.getContentAsString(), getClazz());
		log.info("Updated existing entity: {}", actualEntity);
		assertNotEquals(actualEntity, null);
		assertFalse(actualEntity.isNew());
		assertEquals(existingEntity, actualEntity);
		assertEquals(existingEntity.getClass(), actualEntity.getClass());
		assertEquals(existingEntity.hashCode(), actualEntity.hashCode());
		assertEquals(existingEntity.getId(), actualEntity.getId());
		
		additionalSuccessAssertions(updatedEntity, actualEntity);
	}

	@Test
	void testUpdateFailureMissingEntityException() throws Exception {
		final var existingEntity = entities.get(0);
		
		//GIVEN
		var request = requestUpdatedEntity(existingEntity);

		//MOCK
		whenFindEntity(request.toEntity()).
				thenReturn(Optional.ofNullable(null));
		
		T updatedEntity = request.toEntity();
		assertTrue(updatedEntity.isNew());
		updatedEntity.setId(existingEntity.getId());
		
		//WHEN
		log.info("Perform PUT for URL: {}", getEndPoint());
		var result = mockMvc.perform(put(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}

	@Test
	void testDeleteByKeySuccess() throws Exception {
		final var entity = entities.get(0);
		
		//GIVEN
		Map<String, String> request = retrieveByKeyRequest(false, entity);
		
		//MOCK
		whenFindEntity(entity).thenReturn(Optional.of(entity));
		
		//WHEN
		log.info("Perform DELETE for URL: {}", getEndPoint());
		var result = mockMvc.perform(delete(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		log.info("Response received: {}", response.getContentAsString());
		
		var actualEntity = mapper.readValue(response.getContentAsString(), getClazz());
		log.info("Deleted entity: {}", actualEntity);
		assertNotEquals(actualEntity, null);
		assertFalse(actualEntity.isNew());
		assertEquals(entity, actualEntity);
		assertEquals(entity.getClass(), actualEntity.getClass());
		assertEquals(entity.hashCode(), actualEntity.hashCode());
		assertEquals(entity.getId(), actualEntity.getId());
		
		additionalSuccessAssertions(entity, actualEntity);
	}

	@Test
	void testDeleteByKeyFailureMissingEntityException() throws Exception {
		final var entity = entities.get(0);
		
		//GIVEN
		Map<String, String> request = retrieveByKeyRequest(false, entity);
		
		//MOCK
		whenFindEntity(entity).thenReturn(Optional.ofNullable(null));
		
		//WHEN
		log.info("Perform DELETE for URL: {}", getEndPoint());
		var result = mockMvc.perform(delete(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}

	protected abstract OngoingStubbing<Optional<T>> whenFindEntity(T entity);

	protected abstract RequestEntity<T> requestUpdatedEntity(T existingEntity);

	protected abstract void additionalSuccessAssertions(T existingEntity, T actualEntity);
	
	protected Page<T> testRetrieveByPage(int number, int limit) throws Exception {
		//MOCK
		final var request = PageRequest.of(number, limit);
		var page = page(entities, request);
		when(getService().getCrudManager().getRepository().
				findAll(request)).thenReturn(page);

		//WHEN
	    var result = mockMvc.perform(get(url(getEndPoint(), number, limit)).
			accept(MediaType.APPLICATION_JSON_VALUE)).
	    	andReturn();

	    //THEN
	    var response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
	    log.info("Response received: {}", response.getContentAsString());
	    
	    assertEquals(mapper.writeValueAsString(page), response.getContentAsString());
		assertEquals(Integer.valueOf(entities.size()).longValue(), page.getTotalElements());
		assertEquals(number, page.getNumber());
		
		var items = page.getContent();
		for(int i=0; i<items.size(); i++) {
			var item = items.get(i);
			var entity = entities.get((number * limit) + i);
			assertEquals(item, entity);
			assertEquals(item.hashCode(), entity.hashCode());
		}
		
		return page;
	}

	protected List<T> entities(String path, Class<T> clazz) throws 
	JsonParseException, JsonMappingException, IOException {
		log.info("Loading {} entities from: {}...", clazz, path);
		JavaType type = mapper.getTypeFactory().
				constructCollectionType(List.class, clazz);
		return Collections.unmodifiableList(mapper.readValue(
				new ClassPathResource(path).getInputStream(),
				type));
	}

	protected PageImpl<T> page(List<T> entities, PageRequest page) {
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
	
	protected String url(String endPoint, int number, int limit) {
		log.info("Generating URL for endpoint: {}, number: {} and limit: {}", endPoint, number, limit);
		return new StringBuilder(getEndPoint()).append("/").
				append(number).append("?limit=").append(limit).toString();
	}
	
	protected final Map<String, String> retrieveByKeyRequest(boolean testForNullKey, T entity) {
		Map<String, String> keys = null;
		if(testForNullKey) {
			keys = Collections.emptyMap();
		} else {
			keys = retrieveByKeyRequest(entity);
		}
		return keys;
	}

	protected abstract Class<T> getClazz();

	protected abstract String getEndPoint();

	protected abstract RequestEntity<T> requestEntity(T entity);

	protected abstract String path();
	
	protected abstract Map<String, String> retrieveByKeyRequest(T entity);
	
	protected abstract AbstractCrudController<T> getService();

}
