package net.zoostar.hc.dao;

import java.util.Optional;

import net.zoostar.hc.model.User;

public interface UserRepository extends StringPagingAndSortingRepository<User> {
	Optional<User> findByEmail(String email);
}
