package net.zoostar.hc.web.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class DummyAuthenticationProviderImpl implements AuthenticationProvider, UserDetailsService, InitializingBean {

	private static final String USERNAME = "devops@zoostar.net";
	private static final String CREDENTIAL = "Hello!23";

	protected UserDetails userDetails;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authenticated = null;
		var email = authentication.getName().trim();
		log.info("Authenticating: {}...", email);
		if(USERNAME.equalsIgnoreCase(email) &&
				CREDENTIAL.equals(authentication.getCredentials().toString())) {
			UserDetails userDetails = loadUserByUsername(email);
			log.info("User details loaded: {}.", userDetails);
			authenticated = new UsernamePasswordAuthenticationToken(
					userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
			log.info("{} authenticated successfully.", email);
		} else {
			throw new BadCredentialsException("Credentials should match username/password: " + USERNAME + "/" + CREDENTIAL);
		}
		return authenticated;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return UsernamePasswordAuthenticationToken.class.equals(clazz);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userDetails;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(userDetails == null) {
			initUserDetails();
		}
	}

	protected void initUserDetails() {
		log.info("{}", "Creating the following dummy credentials to logon to DEV env...");
		log.info("Credentials: {}/{}", USERNAME, CREDENTIAL);
		this.userDetails = new UserDetails() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Collections.emptyList();
			}

			@Override
			public String getPassword() {
				return CREDENTIAL;
			}

			@Override
			public String getUsername() {
				return USERNAME;
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
			
		};
	}

}
