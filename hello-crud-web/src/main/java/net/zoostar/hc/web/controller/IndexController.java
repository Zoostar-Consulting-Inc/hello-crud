package net.zoostar.hc.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Controller
public class IndexController {
	
	@Value("${env.value}")
	protected String envValue;
	
	@Value("${app.name}")
	protected String appName;
	
	@GetMapping(value="/", produces=MediaType.TEXT_HTML_VALUE)
	public ModelAndView loadHomePage(Map<String, Object> model) {
		log.info("Loading home page index.jsp message {} for env: {}...", appName, envValue);
		model.put("env", getEnvValue());
		model.put("message", getAppName());
		return new ModelAndView("index", model);
	}
}
