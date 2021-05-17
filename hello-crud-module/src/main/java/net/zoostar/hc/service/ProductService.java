package net.zoostar.hc.service;

import org.springframework.data.domain.Page;

import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.impl.DuplicateEntityException;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.validate.ValidatorException;

public interface ProductService {

	ProductRepository getRepository();
	
	Product create(Product product) throws ValidatorException, DuplicateEntityException;

	Page<Product> retrieve(int number, int limit);

	Product update(Product product) throws ValidatorException, MissingEntityException;

	Product delete(String sku) throws MissingEntityException;

}
