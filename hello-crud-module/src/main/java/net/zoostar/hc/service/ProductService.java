package net.zoostar.hc.service;

import org.springframework.data.domain.Page;

import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.validate.ValidatorException;

public interface ProductService {

	ProductRepository getRepository();
	
	Product create(Product entity) throws ValidatorException;

	Page<Product> retrieve(int number, int limit);

}
