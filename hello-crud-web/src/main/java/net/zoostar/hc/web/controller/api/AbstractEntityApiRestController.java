package net.zoostar.hc.web.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.service.impl.DuplicateEntityException;
import net.zoostar.hc.validate.ValidatorException;
import net.zoostar.hc.web.request.RequestEntity;

public abstract class AbstractEntityApiRestController<T extends AbstractStringPersistable> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected ResponseEntity<T> create(@RequestBody RequestEntity<T> request, Class<T> clazz) {
		log.info("Creating new entity: {}...", request);
		ResponseEntity<T> response = null;
		try {
			response = new ResponseEntity<>(getCrudManager().create(
					request.toEntity()), HttpStatus.CREATED);
		} catch (ValidatorException | DuplicateEntityException e) {
			log.warn(e.getMessage());
			response = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
		return response;
	}

	protected abstract StringPersistableCrudService<T> getCrudManager();

}
