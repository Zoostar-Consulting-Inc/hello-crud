package net.zoostar.hc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.validate.Validator;
import net.zoostar.hc.validate.ValidatorException;

@Transactional(readOnly = true)
public abstract class AbstractCrudServiceImpl<T extends AbstractStringPersistable>
implements StringPersistableCrudService<T>, InitializingBean {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected Validator<T> businessKeyValidator;
	
	protected Validator<T> duplicateEntityValidator;

	protected Validator<T> missingEntityValidator;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		businessKeyValidator = businessKeyValidator();
		duplicateEntityValidator = duplicateEntityValidator();
		missingEntityValidator = missingEntityValidator();
		init();
	}

	@Override
	@Transactional(readOnly = false)
	public T create(T persistable) throws ValidatorException {
		preCreateFilter(persistable);
		log.info("Persisting new entity: {}...", persistable);
		return getRepository().save(persistable);
	}

	@Override
	public Page<T> retrieve(int number, int limit) {
		preRetrieveValidation(number, limit);
		log.info("Retrieving {} entities for page {}...", limit, number);
		return getRepository().findAll(PageRequest.of(number, limit));
	}

	@Override
	@Transactional(readOnly = false)
	public final T update(T persistable) throws ValidatorException {
		preUpdateFilter(persistable);
		log.info("Updating entity: {}...", persistable);
		var entity = retrieveByKey(persistable);
		persistable.setId(entity.getId());
		return getRepository().save(persistable);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(String id) {
		log.info("Deleting entity by ID: {}", id);
		getRepository().deleteById(id);
	}

	private final void preCreateFilter(T persistable) throws ValidatorException {
		preNullValidation(persistable);
		log.info("Proceeding with validations for entity: {}...", persistable);
		preCreateValidation(persistable);
		duplicateEntityValidator.validate(persistable);
	}

	private final void preUpdateFilter(T persistable) throws ValidatorException {
		preNullValidation(persistable);
		log.info("Proceeding with validations for entity: {}...", persistable);
		preUpdateValidation(persistable);
		missingEntityValidator.validate(persistable);
	}

	protected void preCreateValidation(T persistable) throws ValidatorException {
	}

	protected void preRetrieveValidation(int number, int limit) {
		if(number < 0) {
			throw new IllegalArgumentException("Page number may not be less than 0!");
		}

		if(limit < 1) {
			throw new IllegalArgumentException("Page limit has to be more than 0!");
		}
	}

	protected void preUpdateValidation(T entity) throws ValidatorException {
	}

	protected Validator<T> duplicateEntityValidator() {
		return new Validator<>() {

			@Override
			public void validate(T persistable) throws DuplicateEntityException {
				log.info("Validating duplicate entity for: {}...", persistable);
				try {
					T entity = retrieveByKey(persistable);
					throw new DuplicateEntityException("Found existing entity with ID: " + entity.getId());
				} catch(MissingEntityException e) {
					log.info("No duplicate entity found for: {}.", persistable);
				}
			}

		};
	}

	protected Validator<T> missingEntityValidator() {
		return new Validator<>() {

			@Override
			public void validate(T persistable) throws ValidatorException {
				log.info("Validate whether entity exists: {}...", persistable);
				T entity = retrieveByKey(persistable);
				log.info("Entity found: {}.", entity);
			}
			
		};
	}
	
	private void preNullValidation(T persistable) {
		if(persistable == null) {
			throw new IllegalArgumentException("Entity may not be null!");
		}
	}
	
	protected abstract Validator<T> businessKeyValidator() throws ValidatorException;

	protected abstract T retrieveByKey(T persistable) throws MissingEntityException;
	
	protected abstract void init() throws Exception;
	
}
