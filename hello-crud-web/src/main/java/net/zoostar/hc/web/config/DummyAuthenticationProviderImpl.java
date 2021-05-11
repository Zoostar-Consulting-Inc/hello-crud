package net.zoostar.hc.web.config;

import java.util.Collections;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class DummyAuthenticationProviderImpl implements AuthenticationProvider, InitializingBean {

	private static final String USERNAME = "devops@zoostar.net";
	private static final String CREDENTIAL = "Hello!23";
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authenticated = null;
		String email = authentication.getName().trim();
		String cred = authentication.getCredentials().toString();
		log.info("Authenticating: {}...", email);
		if(USERNAME.equalsIgnoreCase(email) && CREDENTIAL.equals(cred)) {
			authenticated = new UsernamePasswordAuthenticationToken(
					USERNAME, CREDENTIAL, Collections.emptyList());
			log.info("{} authenticated successfully.", email);
		} else {
			throw new BadCredentialsException("Credentials should match username/password: " + USERNAME + "/" + CREDENTIAL);
		}
		return authenticated;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("{}", "Use the following dummy credentials to logon to DEV env...");
		log.info("Credentials: {}/{}", USERNAME, CREDENTIAL);
	}

}