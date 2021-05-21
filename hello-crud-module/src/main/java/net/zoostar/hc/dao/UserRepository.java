package net.zoostar.hc.dao;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import net.zoostar.hc.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
	Optional<User> findByEmail(String emails);
}
