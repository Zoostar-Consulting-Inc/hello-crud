package net.zoostar.hc.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface StringPagingAndSortingRepository<T> extends PagingAndSortingRepository<T, String> {

}
