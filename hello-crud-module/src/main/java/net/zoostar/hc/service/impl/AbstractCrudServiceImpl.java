package net.zoostar.hc.service.impl;

import org.springframework.transaction.annotation.Transactional;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.validate.ValidatorException;

@Transactional(readOnly = true)
public abstract class AbstractCrudServiceImpl<T extends AbstractStringPersistable>
implements StringPersistableCrudService<T> {
	
	@Override
	@Transactional(readOnly = false)
	public final T create(T persistable) throws ValidatorException, DuplicateEntityException {
		preCreateValidation(persistable);
		return persist(persistable);
	}

	protected abstract void preCreateValidation(T persistable) throws ValidatorException;
	protected abstract T persist(T persistable) throws DuplicateEntityException;
}
