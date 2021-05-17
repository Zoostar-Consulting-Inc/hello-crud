package net.zoostar.hc.dao;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.zoostar.hc.model.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
	Optional<Product> findBySku(String sku);
}
