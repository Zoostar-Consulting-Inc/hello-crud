package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.ProductService;
import net.zoostar.hc.validate.MissingRequiredFieldException;
import net.zoostar.hc.validate.Validator;
import net.zoostar.hc.validate.ValidatorException;

@Slf4j
@Getter
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	protected ProductRepository repository;
	
	protected final Validator<Product> productSkuValidator = productSkuValidator();

	@Override
	public Product create(Product product) throws ValidatorException, DuplicateEntityException {
		preCreateValidation(product);
		
		if(getRepository().findBySku(product.getSku()).isPresent()) {
			throw new DuplicateEntityException();
		}
		
		return repository.save(product);
	}

	@Override
	public Page<Product> retrieve(int number, int limit) {
		return repository.findAll(PageRequest.of(number, limit));
	}

	protected void preCreateValidation(Product product) throws ValidatorException {
		productSkuValidator.validate(product);
	}

	protected Validator<Product> productSkuValidator() {
		return new Validator<Product>() {

			@Override
			public void validate(Product product) throws ValidatorException {
				if(product == null || StringUtils.isBlank(product.getSku())) {
					throw new MissingRequiredFieldException("Missing Required Field: sku");
				}
				log.info("Passed Product SKU Validation: {}", product);
			}

		};
	}

	@Override
	public Product update(Product product) throws ValidatorException, MissingEntityException {
		preUpdateValidation(product);

		var optional = getRepository().findBySku(product.getSku());
		if(optional.isEmpty()) {
			throw new MissingEntityException();
		} else {
			product.setId(optional.get().getId());
		}
		
		return repository.save(product);
	}

	private void preUpdateValidation(Product product) throws ValidatorException {
		productSkuValidator.validate(product);
	}

}
