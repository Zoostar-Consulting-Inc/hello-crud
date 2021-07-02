package net.zoostar.hc.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MdmUser implements EntityWrapper<User>, MultiSourceEntity {

	private long id;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String source;

	@Override
	public User toEntity() {
		var user = new User();
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setSource(source);
		return user;
	}

}
