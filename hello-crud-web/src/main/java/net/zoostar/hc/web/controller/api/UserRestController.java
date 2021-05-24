package net.zoostar.hc.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.web.request.RequestUser;

@Setter
@Getter
@RestController
@RequestMapping(path = "/api/user")
public class UserRestController extends AbstractEntityApiRestController<User> {

	@Autowired
	protected UserService userManager;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody RequestUser request) {
		return super.create(request, User.class);
	}

	@Override
	protected StringPersistableCrudService<User> getCrudManager() {
		return userManager;
	}
}
