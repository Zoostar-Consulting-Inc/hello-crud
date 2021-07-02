package net.zoostar.hc.web.request;

import lombok.ToString;
import net.zoostar.hc.model.EntityWrapper;
import net.zoostar.hc.model.User;

@ToString
public class RequestUser implements EntityWrapper<User> {
	
	private User user;
	
	public RequestUser() {
		this.user = new User();
	}
	
	public RequestUser(User user) {
		this();
		this.user.setEmail(user.getEmail());
		this.user.setFirstName(user.getFirstName());
		this.user.setLastName(user.getLastName());
		this.user.setSource(user.getSource());
	}

	@Override
	public User toEntity() {
		return user;
	}

	public String getEmail() {
		return user.getEmail();
	}

	public void setEmail(String email) {
		user.setEmail(email);
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	public void setFirstName(String firstName) {
		user.setFirstName(firstName);
	}

	public String getLastName() {
		return user.getLastName();
	}

	public void setLastName(String lastName) {
		user.setLastName(lastName);
	}
	
	public String getSource() {
		return user.getSource();
	}
	
	public void setSource(String source) {
		user.setSource(source);
	}

}
