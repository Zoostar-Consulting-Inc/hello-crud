package net.zoostar.hc.model;


import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Product extends AbstractStringPersistable {

	@Column(nullable = false, length = 20)
	private String sku;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	@Column(nullable = true, length = 50)
	private String desc;

	public Product(Product copy) {
		this.sku = copy.getSku();
		this.name = copy.getName();
		this.desc = copy.getDesc();
	}

	@Override
	public int hashCode() {
		return Objects.hash(sku);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Product other = (Product) obj;
		return Objects.equals(sku, other.sku);
	}
}
