package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.zoostar.hc.dao.UserRepository;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.UserServiceImpl;
import net.zoostar.hc.web.request.RequestUser;

@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
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
		var response = result.getResponse();
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
		
		var user = mapper.readValue(response.getContentAsString(), User.class);
		log.info("Created new entity: {}", user);
		assertNotEquals(entity, null);
		assertEquals(entity, user);
		assertEquals(entity.getClass(), user.getClass());
		assertEquals(entity.hashCode(), user.hashCode());
		assertFalse(StringUtils.isBlank(user.getEmail()));
		assertEquals(entity.getId(), user.getId());
		assertEquals(entity.getEmail(), entity.getEmail());
		assertEquals(entity.getFirstName(), user.getFirstName());
		assertEquals(entity.getLastName(), user.getLastName());
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
