package net.zoostar.hc.batch.tasklet;

import org.springframework.batch.item.ItemProcessor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.model.MdmUser;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.impl.UserServiceImpl;

@Slf4j
@Getter
@Setter
public class UserRecordProcessor extends UserServiceImpl
implements ItemProcessor<MdmUser, User> {
	
	@Override
	public User process(MdmUser item) throws Exception {
		log.debug("Processing item: {}...", item);
		var user = item.toEntity();
		preCreateFilter(user);
		return user;
	}

}
