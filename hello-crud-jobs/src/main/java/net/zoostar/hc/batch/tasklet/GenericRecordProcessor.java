package net.zoostar.hc.batch.tasklet;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.AbstractStringPersistable;
import net.zoostar.hc.model.EntityWrapper;

@Slf4j
public class GenericRecordProcessor<E extends EntityWrapper<T>, T extends AbstractStringPersistable>
implements ItemProcessor<E, T> {
	
	@Override
	public T process(E item) throws Exception {
		log.debug("Processing item: {}...", item);
		return item.toEntity();
	}

}
