package net.zoostar.hc.web.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.web.request.RequestUser;

@Getter
@RestController
@RequestMapping(path = "/api/user")
public class UserRestController extends AbstractCrudController<User> {
	
	protected UserService crudManager;
	
	@Autowired
	public void setCrudManager(UserService crudManager) {
		this.crudManager = crudManager;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody RequestUser request) {
		return super.create(request);
	}
	
	@Override
	@GetMapping(value = "/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<User>> retrieve(@PathVariable int number, @RequestParam int limit) {
		return super.retrieve(number, limit);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> update(@RequestBody RequestUser request) {
		return super.update(request);
	}
	
	@Override
	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> deleteByKey(@RequestBody Map<String, String> request) {
		return super.deleteByKey(request);
	}

	@Override
	@PostMapping(value = "/retrieve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> retrieveByKey(@RequestBody Map<String, String> request) {
		return super.retrieveByKey(request);
	}

	@Override
	protected User retrieveEntity(Map<String, String> request) throws MissingEntityException {
		return getCrudManager().retrieveByEmail(request.get("email"));
	}
	
}
