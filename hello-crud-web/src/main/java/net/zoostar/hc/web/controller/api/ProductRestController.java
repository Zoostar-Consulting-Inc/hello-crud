package net.zoostar.hc.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.web.request.RequestProduct;
import net.zoostar.hw.model.Product;
import net.zoostar.hw.service.ProductService;
import net.zoostar.hw.validate.ValidatorException;

@Slf4j
@Setter
@Getter
@RestController
@RequestMapping(value="/secured/api/product")
public class ProductRestController {
	
	@Autowired
	protected ProductService productManager;
	
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> create(@RequestBody RequestProduct request) {
		log.info("Creating new record: {}...", request);
		ResponseEntity<Product> response = null;
		try {
			response = new ResponseEntity<>(productManager.create(request.toEntity()), HttpStatus.CREATED);
		} catch (ValidatorException e) {
			response = new ResponseEntity<>(request.toEntity(), HttpStatus.EXPECTATION_FAILED);
		}
		return response;
	}
	
	@GetMapping(value="/{number}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Product>> retrieve(@PathVariable int number, @RequestParam int limit) {
		log.info("Retreiving {} Products for page: {}...", limit, number);
		return new ResponseEntity<>(productManager.retrieve(number, limit), HttpStatus.OK);
	}
}
