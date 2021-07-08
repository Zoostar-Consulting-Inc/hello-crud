package net.zoostar.hc.batch.tasklet;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.DataClassRowMapper;

import lombok.Setter;
import net.zoostar.hc.model.MultiSourceEntity;

@Setter
public class MultiSourceDataClassRowMapper<T extends MultiSourceEntity> extends DataClassRowMapper<T> {

	protected String source;

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T entity = super.mapRow(rs, rowNum);
		entity.setSource(source);
		return entity;
	}
}
