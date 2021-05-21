package net.zoostar.hc.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.service.impl.DuplicateEntityException;
import net.zoostar.hc.validate.ValidatorException;
import net.zoostar.hc.web.request.RequestUser;

@Slf4j
@Setter
@Getter
@RestController
@RequestMapping(value="/api/user")
public class UserRestController {

	@Autowired
	protected UserService userManager;
	
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody RequestUser request) {
		log.info("Creating new entity: {}...", request);
		ResponseEntity<User> response = null;
		try {
			response = new ResponseEntity<>(userManager.create(request.toEntity()), HttpStatus.CREATED);
		} catch (ValidatorException | DuplicateEntityException e) {
			log.warn(e.getMessage());
			response = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
		return response;
	}
}
