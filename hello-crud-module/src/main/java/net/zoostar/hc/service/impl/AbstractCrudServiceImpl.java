package net.zoostar.hc.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.StringPersistableCrudService;
import net.zoostar.hc.validate.ValidatorException;

@Transactional(readOnly = true)
public abstract class AbstractCrudServiceImpl<T extends AbstractStringPersistable>
implements StringPersistableCrudService<T> {
	
//	private final Validator<T> validator = productSkuValidator();

	@Override
	@Transactional(readOnly = false)
	public final T create(T persistable) throws ValidatorException, DuplicateEntityException {
		preCreateValidation(persistable);
		return persist(persistable);
	}

	@Override
	public Page<T> retrieve(int number, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T update(T entity) throws ValidatorException, MissingEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T delete(String id) throws MissingEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract void preCreateValidation(T persistable) throws ValidatorException;
	protected abstract T persist(T persistable) throws DuplicateEntityException;
}
