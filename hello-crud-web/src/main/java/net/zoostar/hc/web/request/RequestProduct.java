package net.zoostar.hc.web.request;

import lombok.ToString;
import net.zoostar.hc.model.Product;

@ToString
public class RequestProduct implements RequestEntity<Product> {
	
	private Product product;

	public RequestProduct() {
		product = new Product();
	}
	
	public RequestProduct(Product entity) {
		this();
		this.product.setDesc(entity.getDesc());
		this.product.setName(entity.getName());
		this.product.setSku(entity.getSku());
		this.product.setSource(entity.getSource());
	}

	public void setSku(String sku) {
		product.setSku(sku);
	}
	
	public String getSku() {
		return product.getSku();
	}
	
	public void setName(String name) {
		product.setName(name);
	}
	
	public String getName() {
		return product.getName();
	}
	
	public void setDesc(String desc) {
		product.setDesc(desc);
	}
	
	public String getDesc() {
		return product.getDesc();
	}
	
	public String getSource() {
		return product.getSource();
	}
	
	public void setSource(String source) {
		product.setSource(source);
	}
	
	@Override
	public Product toEntity() {
		return product;
	}

}
