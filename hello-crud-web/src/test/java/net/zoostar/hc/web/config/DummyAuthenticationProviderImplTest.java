package net.zoostar.hc.web.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.zoostar.hc.web.AbstractMockTestHarness;

class DummyAuthenticationProviderImplTest extends AbstractMockTestHarness {

	UsernamePasswordAuthenticationToken authentication;
	DummyAuthenticationProviderImpl authenticationProvider;
	
	@BeforeEach
	protected void beforeEach(TestInfo test) throws JsonParseException, JsonMappingException, IOException {
		super.beforeEach(test);
		authenticationProvider = new DummyAuthenticationProviderImpl();
	}
	
	@Test
	void testAuthenticateSuccess() throws JsonParseException, JsonMappingException, IOException {
		//GIVEN
		String email = "devops@zoostar.net";
		String password = "Hello!23";
		authentication = new UsernamePasswordAuthenticationToken(email, password);
		assertTrue(authenticationProvider.supports(authentication.getClass()));
		
		//WHEN
		Authentication authenticated = authenticationProvider.authenticate(authentication);
		
		//THEN
		assertNotNull(authenticated);
		assertEquals(email, authenticated.getName());
		assertEquals(password, authenticated.getCredentials().toString());
	}
	
	@Test
	void testAuthenticateUserNotFound() throws JsonParseException, JsonMappingException, IOException {
		//GIVEN
		String email = "user1@email.com";
		authentication = new UsernamePasswordAuthenticationToken(email, "Hello!23");
		assertTrue(authenticationProvider.supports(authentication.getClass()));
		
		//WHEN
		final ThrowingRunnable throwingRunnable = () -> authenticationProvider.authenticate(authentication);
		
		//THEN
		Assert.assertThrows(BadCredentialsException.class, throwingRunnable);
	}
	
	@Test
	void testAuthenticateIncorrectPassword() throws JsonParseException, JsonMappingException, IOException {
		//GIVEN
		String email = "devops@zoostar.net";
		authentication = new UsernamePasswordAuthenticationToken(email, "Hello123");
		assertTrue(authenticationProvider.supports(authentication.getClass()));
		
		//WHEN
		final ThrowingRunnable throwingRunnable = () -> authenticationProvider.authenticate(authentication);
		
		//THEN
		Assert.assertThrows(BadCredentialsException.class, throwingRunnable);
	}

}
