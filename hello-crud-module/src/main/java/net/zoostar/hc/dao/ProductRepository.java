package net.zoostar.hc.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.zoostar.hc.model.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
	
}
