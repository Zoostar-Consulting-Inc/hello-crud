package net.zoostar.hc.dao;

import java.util.Optional;

import net.zoostar.hc.model.Product;

public interface ProductRepository extends StringPagingAndSortingRepository<Product> {
	Optional<Product> findBySku(String sku);
}
