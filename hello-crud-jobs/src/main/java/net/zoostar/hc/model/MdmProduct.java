package net.zoostar.hc.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MdmProduct implements EntityWrapper<Product>, MultiSourceEntity {

	private long id;
	
	private String prodSku;
	
	private String prodName;
	
	private String prodDesc;
	
	private String source;

	@Override
	public Product toEntity() {
		var product = new Product();	
		product.setDesc(prodDesc);
		product.setName(prodName);
		product.setSku(prodSku);
		product.setSource(source);
		return product;
	}

}
