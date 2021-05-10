package net.zoostar.hc.web.config;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	static final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);
	
	@Value("${request.handler.selectors.base.package}")
	protected String requestHandlerSelectorsBasePackage;

	@Bean
	public Docket getApi() {
		log.debug("{}", "Loading Swagger Docket...");
		return new Docket(DocumentationType.SWAGGER_2).
				apiInfo(getApiInfo()).
				select().
				apis(RequestHandlerSelectors.basePackage(requestHandlerSelectorsBasePackage)).
				paths(PathSelectors.any()).
				build();
	}

	protected ApiInfo getApiInfo() {
		return new ApiInfo("Hello World", "My Hello World Application",
				"1.1.0", "", new Contact("Zoostar Consulting, Inc.", "", "devops@zoostar.net"),
				"", "", Collections.emptyList());
	}

}
