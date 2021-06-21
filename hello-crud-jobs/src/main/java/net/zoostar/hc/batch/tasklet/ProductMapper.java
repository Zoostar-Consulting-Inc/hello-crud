package net.zoostar.hc.batch.tasklet;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.Product;

@Slf4j
public class ProductMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		log.debug("Mapping item for row number: {}", rowNum);
		Product product = new Product();
		product.setDesc(rs.getString("desc"));
		product.setName(rs.getString("name"));
		product.setSku(rs.getString("sku"));
		return product;
	}

}
