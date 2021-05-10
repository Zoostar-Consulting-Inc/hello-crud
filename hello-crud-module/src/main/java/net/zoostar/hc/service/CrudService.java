package net.zoostar.hc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.zoostar.hc.exception.EntityAlreadyExistsException;
import net.zoostar.hc.validate.ValidatorException;

public interface CrudService<T, E> {
	PagingAndSortingRepository<T, E> getRepository();
	T create(T entity) throws EntityAlreadyExistsException, ValidatorException;
	Page<T> retrieve(int number, int limit);
}
