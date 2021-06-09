package net.zoostar.hc.service;

import org.springframework.data.domain.Page;

import net.zoostar.hc.dao.StringPagingAndSortingRepository;
import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.validate.ValidatorException;

public interface StringPersistableCrudService<T extends AbstractStringPersistable> {

	<R extends StringPagingAndSortingRepository<T>> R getRepository();
	
	T create(T persistable) throws ValidatorException;

	Page<T> retrieve(int number, int limit);

	T update(T entity) throws ValidatorException;

	void delete(String id);
}
