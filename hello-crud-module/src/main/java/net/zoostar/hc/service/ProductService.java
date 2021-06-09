package net.zoostar.hc.service;

import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.impl.MissingEntityException;

public interface ProductService extends StringPersistableCrudService<Product> {
	Product retrieveBySku(String sku) throws MissingEntityException;
}
