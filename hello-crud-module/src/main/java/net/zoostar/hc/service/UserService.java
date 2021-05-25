package net.zoostar.hc.service;

import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.MissingEntityException;

public interface UserService extends StringPersistableCrudService<User> {
	User retrieveByEmail(String email) throws MissingEntityException;
}
