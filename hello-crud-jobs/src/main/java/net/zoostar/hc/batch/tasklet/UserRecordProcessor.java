package net.zoostar.hc.batch.tasklet;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.UserServiceImpl;

@Slf4j
public class UserRecordProcessor extends UserServiceImpl
implements ItemProcessor<User, User> {

	@Override
	public User process(User item) throws Exception {
		log.debug("Processing item: {}...", item);
		preCreateFilter(item);
		return item;
	}

}
