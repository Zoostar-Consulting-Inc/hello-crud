package net.zoostar.hc.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.web.filter.GatewayAuditFilterChain;

@Slf4j
@WebAppConfiguration
@ActiveProfiles({"dev", "hsql-dev"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext-web.xml"})
class IndexControllerTest {

	@Autowired
	WebApplicationContext wac;
	
	MockMvc mockMvc;
	
	@BeforeEach
	public void beforeEach(TestInfo test) throws JsonParseException, JsonMappingException, IOException {
		System.out.println();
		log.info("Executing test: [{}]...", test.getDisplayName());
		
		GatewayAuditFilterChain filter = new GatewayAuditFilterChain();
		Assert.assertNull(GatewayAuditFilterChain.GATEWAY_AUDIT_HOLDER.get());
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).
				addFilters(filter).build();
	}

	@Test
	void testLoadHomePage() throws Exception {
		//WHEN
	    MvcResult result = mockMvc.perform(get("/")).andReturn();
	    
		//THEN
		assertNotNull(result);
		ModelAndView modelAndView = result.getModelAndView();
		assertEquals("index", modelAndView.getViewName());
		assertEquals("dev", modelAndView.getModel().get("env"));
		assertEquals("Hello CRUD", modelAndView.getModel().get("message"));
	}

	@Test
	void testExcludedUrlPattern() throws Exception {
		//WHEN
	    MvcResult result = mockMvc.perform(get("/hw/login")).andReturn();
	    
		//THEN
		assertNotNull(result);
	}

}
