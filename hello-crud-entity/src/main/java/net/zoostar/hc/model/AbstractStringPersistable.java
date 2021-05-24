package net.zoostar.hc.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractStringPersistable implements Persistable<String>, MultiSourceEntity {
	
	@Id
	@Column(length = 50)
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name="uuid", strategy="uuid2")
	private String id;
	
	@Column(length = 10)
	private String source;

	@JsonIgnore
	@Override
	public boolean isNew() {
		return StringUtils.isBlank(id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source);
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
		AbstractStringPersistable other = (AbstractStringPersistable) obj;
		return Objects.equals(source, other.source);
	}
	
}
