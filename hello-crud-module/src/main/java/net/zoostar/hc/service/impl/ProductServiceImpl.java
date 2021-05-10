package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.ProductService;
import net.zoostar.hc.validate.MissingRequiredFieldException;
import net.zoostar.hc.validate.Validator;
import net.zoostar.hc.validate.ValidatorException;

@Getter
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	protected ProductRepository repository;

	@Override
	public Product create(Product product) throws ValidatorException {
		var validator = new Validator<Product>() {

			@Override
			public void validate(Product product) throws ValidatorException {
				if(product == null || StringUtils.isBlank(product.getSku())) {
					throw new MissingRequiredFieldException("Missing Required Field: sku");
				}
			}

		};
		
		validator.validate(product);
		return repository.save(product);
	}

	@Override
	public Page<Product> retrieve(int number, int limit) {
		return repository.findAll(PageRequest.of(number, limit));
	}

}
