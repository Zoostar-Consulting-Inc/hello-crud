package net.zoostar.hc.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import lombok.Getter;
import net.zoostar.hc.dao.ProductRepository;
import net.zoostar.hc.model.EntityWrapper;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.impl.ProductServiceImpl;
import net.zoostar.hc.web.request.RequestProduct;

class ProductRestControllerTest extends AbstractCrudControllerTest<Product> {
	
	@Mock
	protected ProductRepository repository;

	@InjectMocks
	protected ProductServiceImpl crudManager;

	@Getter
	@Autowired
	protected ProductRestController service;

	@Override
	protected void additionalSetup() throws Exception {
		service.setCrudManager(crudManager);
		crudManager.afterPropertiesSet();
	}

	@Test
	void testCreateFailureMissingRequiredFieldSku() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestProduct();
		request.setDesc(entity.getDesc());
		request.setName(entity.getName());
		request.setSource(entity.getSource());

		//WHEN
		log.info("Perform POST for URL: {}", getEndPoint());
		var result = mockMvc.perform(post(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}

	@Test
	void testCreateFailureMissingRequiredFieldSource() throws Exception {
		var entity = entities.get(0);
		
		//GIVEN
		var request = new RequestProduct();
		request.setDesc(entity.getDesc());
		request.setName(entity.getName());
		request.setSku(entity.getSku());

		//WHEN
		log.info("Perform POST for URL: {}", getEndPoint());
		var result = mockMvc.perform(post(getEndPoint()).
	    		contentType(MediaType.APPLICATION_JSON).
	    		content(mapper.writeValueAsString(request)).
	    		accept(MediaType.APPLICATION_JSON_VALUE)).
				andReturn();
		
		//THEN
		var response = result.getResponse();
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), response.getStatus());
	}

	@Override
	protected void additionalSuccessAssertions(Product expectedEntity, Product actualEntity) {
		assertFalse(StringUtils.isBlank(actualEntity.getSku()));
		assertEquals(expectedEntity.getSku(), expectedEntity.getSku());
		assertEquals(expectedEntity.getDesc(), actualEntity.getDesc());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getSource(), actualEntity.getSource());
	}

	protected String path() {
		return "data/products.json";
	}

	@Override
	protected Class<Product> getClazz() {
		return Product.class;
	}

	@Override
	protected String getEndPoint() {
		return "/api/product/secured";
	}

	@Override
	protected RequestProduct requestEntity(Product entity) {
		return new RequestProduct(entity);
	}

	@Override
	protected EntityWrapper<Product> requestUpdatedEntity(Product existingEntity) {
		RequestProduct request = new RequestProduct(existingEntity);
		request.setDesc(existingEntity.getDesc() + " Updated");
		return request;
	}

	@Override
	protected OngoingStubbing<Optional<Product>> whenFindEntity(Product product) {
		return when(repository.findBySku(product.getSku()));
	}

	@Override
	protected Map<String, String> retrieveByKeyRequest(Product product) {
		var keys = new HashMap<String, String>(1);
		keys.put("sku", product.getSku());
		return keys;
	}
}
