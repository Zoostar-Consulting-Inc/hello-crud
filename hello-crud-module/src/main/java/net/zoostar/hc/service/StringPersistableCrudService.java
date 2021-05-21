package net.zoostar.hc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.service.impl.DuplicateEntityException;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.validate.ValidatorException;

public interface StringPersistableCrudService<T extends AbstractStringPersistable> {

	<R extends PagingAndSortingRepository<T, String>> R getRepository();
	
	T create(T persistable) throws ValidatorException, DuplicateEntityException;

	Page<T> retrieve(int number, int limit);

	T update(T entity) throws ValidatorException, MissingEntityException;

	T delete(String id) throws MissingEntityException;
}
