package net.zoostar.hc.model;


import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Product implements Persistable<String> {

	@Id
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name="uuid", strategy="uuid2")
	private String id;

	@Column(nullable = false, length = 20)
	private String sku;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	@Column(nullable = true, length = 50)
	private String desc;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return StringUtils.isBlank(id);
	}
	
	public void setNew(boolean isNew) {
		log.warn("setNew({}) will be ignored!", isNew);
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
