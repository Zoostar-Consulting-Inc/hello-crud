package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.zoostar.hc.dao.UserRepository;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.UserServiceImpl;
import net.zoostar.hc.web.request.RequestUser;

class UserRestControllerTest extends AbstractApiRestController<User> {
	
	private static final String END_POINT = "/api/user";
	
	@Mock
	protected UserRepository repository;

	@InjectMocks
	protected UserServiceImpl manager;
	
	@Autowired
	protected UserRestController service;
	
	@BeforeEach
	public void beforeEach(TestInfo test) throws JsonParseException,
			JsonMappingException, IOException {
		super.beforeEach(test);
		service.setUserManager(manager);
	}

	@Test
	void testCreateSuccess() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestUser();
		request.setEmail(entity.getEmail());
		request.setFirstName(entity.getFirstName());
		request.setLastName(entity.getLastName());

		//MOCK
		when(service.getUserManager().getRepository().
				save(request.toEntity())).thenReturn(entity);

		//WHEN
		log.info("Perform POST for URL: {}", END_POINT);
		var result = mockMvc.perform(post(END_POINT).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		assertTrue(request.toEntity().isNew());
		var response = result.getResponse();
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		
		var user = mapper.readValue(response.getContentAsString(), User.class);
		log.info("Created new entity: {}", user);
		assertNotEquals(user, null);
		assertFalse(entity.isNew());
		assertEquals(entity, user);
		assertEquals(entity.getClass(), user.getClass());
		assertEquals(entity.hashCode(), user.hashCode());
		assertFalse(StringUtils.isBlank(user.getEmail()));
		assertEquals(entity.getId(), user.getId());
		assertEquals(entity.getEmail(), entity.getEmail());
		assertEquals(entity.getFirstName(), user.getFirstName());
		assertEquals(entity.getLastName(), user.getLastName());
	}

	@Test
	void testCreateFailureDuplicateEntity() throws Exception {
		var entity = entities.get(0);
		var email = entity.getEmail();
		
		//GIVEN
		var request = new RequestUser();
		request.setEmail(email);
		request.setFirstName(entity.getFirstName());
		request.setLastName(entity.getLastName());

		//MOCK
		when(repository.findByEmail(email)).thenReturn(Optional.of(entity));

		//WHEN
		log.info("Perform POST for URL: {}", END_POINT);
		var result = mockMvc.perform(post(END_POINT).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}

	@Test
	void testCreateFailureMissingRequiredFieldEmail() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestUser();
		request.setFirstName(entity.getFirstName());
		request.setLastName(entity.getLastName());

		//WHEN
		log.info("Perform POST for URL: {}", END_POINT);
		var result = mockMvc.perform(post(END_POINT).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}

	protected String path() {
		return "data/users.json";
	}

	@Override
	protected String url() {
		return END_POINT;
	}

	@Override
	protected Class<User> classType() {
		return User.class;
	}
}
