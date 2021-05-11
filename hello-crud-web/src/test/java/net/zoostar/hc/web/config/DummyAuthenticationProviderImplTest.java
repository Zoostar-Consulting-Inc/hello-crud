package net.zoostar.hc.web.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/applicationContext-web.xml",
		"classpath:META-INF/spring-security.xml"})
class DummyAuthenticationProviderImplTest {

	UsernamePasswordAuthenticationToken authentication;
	
	@Autowired
	DummyAuthenticationProviderImpl authenticationProvider;
	
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
	void testAuthenticateUserNotFound() {
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
	void testAuthenticateIncorrectPassword() {
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
