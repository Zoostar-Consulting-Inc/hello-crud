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

	@Override
	public int hashCode() {
		final var prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(sku);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Product other = (Product) obj;
		return Objects.equals(sku, other.sku);
	}

}
