package net.zoostar.hc.batch.tasklet;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.Product;

@Slf4j
public class ProductMapper implements RowMapper<Product> {

	@Value("#{jobParameters['source']}")
	protected String source;
	
	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		log.debug("Mapping source item {} for row number: {}", source, rowNum);
		var product = new Product();
		product.setDesc(rs.getString("description"));
		product.setName(rs.getString("name"));
		product.setSku(rs.getString("sku"));
		product.setSource(source);
		return product;
	}

}
