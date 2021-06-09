package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import lombok.Getter;
import lombok.Setter;
import net.zoostar.hc.dao.UserRepository;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.UserServiceImpl;
import net.zoostar.hc.web.request.RequestEntity;
import net.zoostar.hc.web.request.RequestUser;

@Getter
@Setter
class UserRestControllerTest extends AbstractCrudControllerTest<User> {
	
	@Mock
	protected UserRepository repository;

	@InjectMocks
	protected UserServiceImpl crudManager;
	
	@Autowired
	protected UserRestController service;

	@Override
	protected void additionalSetup() throws Exception {
		service.setCrudManager(crudManager);
		crudManager.afterPropertiesSet();
	}

	@Test
	void testCreateFailureMissingRequiredFieldEmail() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestUser();
		request.setFirstName(entity.getFirstName());
		request.setLastName(entity.getLastName());

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
	void testCreateFailureMissingRequiredFieldSource() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestUser();
		request.setEmail(entity.getEmail());
		request.setFirstName(entity.getFirstName());
		request.setLastName(entity.getLastName());

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

	@Override
	protected void additionalSuccessAssertions(User expectedEntity, User actualEntity) {
		assertFalse(StringUtils.isBlank(actualEntity.getEmail()));
		assertEquals(expectedEntity.getEmail(), expectedEntity.getEmail());
		assertEquals(expectedEntity.getFirstName(), actualEntity.getFirstName());
		assertEquals(expectedEntity.getLastName(), actualEntity.getLastName());
		assertEquals(expectedEntity.getSource(), actualEntity.getSource());
	}

	protected String path() {
		return "data/users.json";
	}

	@Override
	protected Class<User> getClazz() {
		return User.class;
	}

	@Override
	protected String getEndPoint() {
		return "/api/user";
	}

	@Override
	protected RequestUser requestEntity(User entity) {
		return new RequestUser(entity);
	}

	@Override
	protected RequestEntity<User> requestUpdatedEntity(User existingEntity) {
		RequestUser request = new RequestUser();
		request.setEmail(existingEntity.getEmail());
		request.setFirstName(existingEntity.getFirstName() + " Updated");
		request.setLastName(existingEntity.getLastName() + " Updated");
		request.setSource(existingEntity.getSource());
		return request;
	}

	@Override
	protected OngoingStubbing<Optional<User>> whenFindEntity(User user) {
		return when(repository.findByEmail(user.getEmail()));
	}

	@Override
	protected Map<String, String> retrieveByKeyRequest(User user) {
		var keys = new HashMap<String, String>(1);
		keys.put("email", user.getEmail());
		return keys;
	}
}
