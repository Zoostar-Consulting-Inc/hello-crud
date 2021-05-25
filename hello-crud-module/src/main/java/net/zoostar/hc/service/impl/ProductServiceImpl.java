package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductServiceImpl extends AbstractCrudServiceImpl<Product>
implements ProductService {

	@Autowired
	protected ProductRepository repository;
	
	@Override
	protected void init() throws Exception {
	}

	@Override
	protected Product retrieveByKey(Product product) throws MissingEntityException {
		return retrieveBySku(product.getSku());
	}

	@Override
	public Product retrieveBySku(String sku) throws MissingEntityException {
		var optional = getRepository().findBySku(sku);
		if(optional.isEmpty()) {
			throw new MissingEntityException("Cannot find entity with sku: " + sku);
		}
		return optional.get();
	}

	@Override
	protected void preCreateValidation(Product product) throws ValidatorException {
		super.preCreateValidation(product);
		businessKeyValidator.validate(product);
	}

	@Override
	protected void preUpdateValidation(Product product) throws ValidatorException {
		super.preUpdateValidation(product);
		businessKeyValidator.validate(product);
		missingEntityValidator.validate(product);
	}

	@Override
	protected Validator<Product> businessKeyValidator() throws ValidatorException {
		return new Validator<>() {

			@Override
			public void validate(Product product) throws MissingRequiredFieldException {
				log.info("Validating business key for: {}...", product);
				if(StringUtils.isBlank(product.getSku())) {
					throw new MissingRequiredFieldException("Missing Required Field: sku");
				}
				if(StringUtils.isBlank(product.getSource())) {
					throw new MissingRequiredFieldException("Missing Required Field: source");
				}
			}

		};
	}

}
