package net.zoostar.hc.web.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.ProductService;
import net.zoostar.hc.service.impl.MissingEntityException;
import net.zoostar.hc.web.request.RequestProduct;

@Getter
@RestController
@RequestMapping(value="/api/product/secured")
public class ProductRestController extends AbstractCrudController<Product> {
	
	protected ProductService crudManager;
	
	@Autowired
	public void setCrudManager(ProductService crudManager) {
		this.crudManager = crudManager;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> create(@RequestBody RequestProduct request) {
		return super.create(request);
	}
	
	@GetMapping(value = "/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Product>> retrieve(@PathVariable int number, @RequestParam int limit) {
		return super.retrieve(number, limit);
	}
	
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> update(@RequestBody RequestProduct request) {
		return super.update(request);
	}
	
	@Override
	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> deleteByKey(@RequestBody Map<String, String> request) {
		return super.deleteByKey(request);
	}

	@PostMapping(value = "/retrieve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> retrieveByKey(@RequestBody Map<String, String> request) {
		return super.retrieveByKey(request);
	}

	@Override
	protected Product retrieveEntity(Map<String, String> request) throws MissingEntityException {
		return getCrudManager().retrieveBySku(request.get("sku"));
	}

}
