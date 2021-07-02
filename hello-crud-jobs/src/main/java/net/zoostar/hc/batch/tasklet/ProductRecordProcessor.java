package net.zoostar.hc.batch.tasklet;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.MdmProduct;
import net.zoostar.hc.model.Product;
import net.zoostar.hc.service.impl.ProductServiceImpl;

@Slf4j
public class ProductRecordProcessor extends ProductServiceImpl
implements ItemProcessor<MdmProduct, Product> {

	@Override
	public Product process(MdmProduct item) throws Exception {
		log.debug("Processing item: {}...", item);
		var product = item.toEntity();
		preCreateFilter(product);
		return product;
	}

}
