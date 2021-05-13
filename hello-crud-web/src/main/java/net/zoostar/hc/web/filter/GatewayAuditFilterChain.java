package net.zoostar.hc.web.filter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GatewayAuditFilterChain extends AbstractRequestLoggingFilter {
	
	private static final List<String> EXCLUDED_ENDPOINTS = Arrays.asList(
			"/hc/signin.html", "/hc/login", "/hc/resources/css/bootstrap.min.css", 
			"/hc/swagger-resources/configuration/ui", "/hc/webjars/springfox-swagger-ui/favicon-32x32.png",
			"/hc/swagger-resources/configuration/security", "/hc/swagger-resources",
			"/hc/v2/api-docs", "/hc/csrf",
			"/hc/resources/css/ie10-viewport-bug-workaround.css", "/hc/resources/css/signin.css",
			"/hc/resources/js/ie-emulation-modes-warning.js", "/hc/resources/js/ie10-viewport-bug-workaround.js"
	);

	public static final ThreadLocal<GatewayAudit> GATEWAY_AUDIT_HOLDER = new ThreadLocal<>();
	
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		if(!exclude(request.getRequestURI())) {
			var auditor = new GatewayAudit();
			auditor.setId(UUID.randomUUID().toString());
			auditor.setUsername(username());
			auditor.setEndPoint(request.getRequestURI());
			auditor.setRemoteAddress(request.getRemoteAddr());
			log.info("Begin Gateway Audit of: {}", auditor.getId());
			auditor.setTime(System.currentTimeMillis());
			GATEWAY_AUDIT_HOLDER.set(auditor);
		}
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		var auditor = GATEWAY_AUDIT_HOLDER.get();
		if(auditor != null) {
			auditor.setDuration(System.currentTimeMillis() - auditor.getTime());
			double time = (double)auditor.getDuration() / 1000;
			log.info("Completed servicing request in {} seconds: {}", time, auditor);
			GATEWAY_AUDIT_HOLDER.remove();
		}
	}
	
	protected String username() {
		var context = SecurityContextHolder.getContext();
		var authentication = context.getAuthentication();
		return authentication == null ? "" : authentication.getName();
	}

	protected boolean exclude(String endpoint) {
		return EXCLUDED_ENDPOINTS.contains(endpoint);
	}

}
