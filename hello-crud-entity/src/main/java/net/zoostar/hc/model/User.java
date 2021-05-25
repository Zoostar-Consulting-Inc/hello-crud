package net.zoostar.hc.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User extends AbstractStringPersistable {
	
	@Column(length = 50)
	private String email;
	
	@Column(length = 50)
	private String firstName;
	
	@Column(length = 50)
	private String lastName;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [email=").append(email).append(", firstName=").append(firstName).append(", lastName=")
				.append(lastName).append(", getId()=").append(getId()).append(", getSource()=").append(getSource())
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(email);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(email, other.email);
	}

}
