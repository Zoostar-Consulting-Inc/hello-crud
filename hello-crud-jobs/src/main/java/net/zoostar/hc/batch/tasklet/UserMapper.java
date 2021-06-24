package net.zoostar.hc.batch.tasklet;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.User;

@Slf4j
public class UserMapper implements RowMapper<User> {

	@Value("#{jobParameters['source']}")
	protected String source;
	
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		log.debug("Mapping item with id {} for row number: {}", rs.getInt("id"), rowNum);
		var user = new User();
		user.setEmail(rs.getString("email"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setSource(source);
		return user;
	}

}
