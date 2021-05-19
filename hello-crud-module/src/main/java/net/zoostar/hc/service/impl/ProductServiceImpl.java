package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

	@Autowired
	protected ProductRepository repository;
	
	private final Validator<Product> productSkuValidator = productSkuValidator();

	@Override
	@Transactional(readOnly = false)
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

	@Override
	@Transactional(readOnly = false)
	public Product update(Product product) throws ValidatorException, MissingEntityException {
		preUpdateValidation(product);

		var optional = getRepository().findBySku(product.getSku());
		if(optional.isEmpty()) {
			throw new MissingEntityException("No entity found to update by SKU: " + product.getSku());
		} else {
			product.setId(optional.get().getId());
		}
		
		return repository.save(product);
	}

	@Override
	@Transactional(readOnly = false)
	public Product delete(String sku) throws MissingEntityException {
		log.info("Deleting by SKU: {}", sku);
		
		var optional = getRepository().findBySku(sku);
		if(optional.isEmpty()) {
			throw new MissingEntityException("No entity found to delete for sku: " + sku);
		}
		
		var entity = optional.get();
		log.info("Found entity to delete: {}", entity);
		
		getRepository().delete(entity);
		return entity;
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

	protected void preUpdateValidation(Product product) throws ValidatorException {
		productSkuValidator.validate(product);
	}

}
