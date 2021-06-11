package net.zoostar.hc.web.controller.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.validate.ValidatorException;
import net.zoostar.hc.web.request.RequestEntity;

public abstract class AbstractCrudController<T extends AbstractStringPersistable> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected ResponseEntity<T> create(RequestEntity<T> request) {
		log.info("{}", "Received request to create a new entity...");
		HttpStatus status = HttpStatus.CREATED;
		T entity = null;
		try {
			entity = getCrudManager().create(request.toEntity());
			log.info("Created new entity: {}.", entity);
		} catch (ValidatorException e) {
			log.warn(e.getMessage());
			status = HttpStatus.EXPECTATION_FAILED;
		}
		return new ResponseEntity<>(entity, status);
	}
	
	protected ResponseEntity<Page<T>> retrieve(int number, @RequestParam int limit) {
		log.info("{}", "Received request to retrieve entities by page...");
		HttpStatus status = HttpStatus.OK;
		Page<T> entities = null;
		try {
			entities = getCrudManager().retrieve(number, limit);
			log.info("Retrieved {} entities for page {}.", entities.getNumberOfElements(), number);
		} catch(IllegalArgumentException e) {
			status = HttpStatus.EXPECTATION_FAILED;
			log.warn(e.getMessage());
		}
		return new ResponseEntity<>(entities, status);
	}

	protected ResponseEntity<T> update(RequestEntity<T> request) {
		log.info("{}", "Received request to update an entity...");
		HttpStatus status = HttpStatus.OK;
		T entity = null;
		try {
			entity = getCrudManager().update(request.toEntity());
			log.info("Updated an existing entity: {}.", entity);
		} catch (ValidatorException e) {
			log.warn(e.getMessage());
			status = HttpStatus.EXPECTATION_FAILED;
		}
		return new ResponseEntity<>(entity, status);
	}

	protected ResponseEntity<T> deleteByKey(Map<String, String> request) {
		log.info("{}", "Received a request to delete an entity by key...");
		T entity = null;
		var status = HttpStatus.OK;
		try {
			entity = retrieveEntity(request);
			getCrudManager().delete(entity.getId());
		} catch(MissingEntityException e) {
			status = HttpStatus.EXPECTATION_FAILED;
			log.warn(e.getMessage());
		}
		return new ResponseEntity<>(entity, status);
	}
	
	protected ResponseEntity<T> retrieveByKey(Map<String, String> request) {
		log.info("{}", "Received a request to retrieve an entity by key...");
		T entity = null;
		var status = HttpStatus.OK;
		try {
			entity = retrieveEntity(request);
			log.info("Entity retrieved: {}", entity);
		} catch(MissingEntityException e) {
			status = HttpStatus.EXPECTATION_FAILED;
			log.warn(e.getMessage());
		}
		return new ResponseEntity<>(entity, status);
	}
	
	protected abstract T retrieveEntity(Map<String, String> request) throws MissingEntityException;

	protected abstract StringPersistableCrudService<T> getCrudManager();
	
}
