package net.zoostar.hc.web.config;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import springfox.documentation.spring.web.plugins.Docket;

class SwaggerConfigTest {

	SwaggerConfig swagger = new SwaggerConfig();
	
	@Test
	void testGetApi() {
		//WHEN
		Docket docket = swagger.getApi();
		
		//THEN
		Assert.assertNotNull(docket);
	}

}
