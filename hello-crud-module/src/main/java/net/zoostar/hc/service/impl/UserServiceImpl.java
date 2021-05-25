package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.zoostar.hc.dao.UserRepository;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.validate.MissingRequiredFieldException;
import net.zoostar.hc.validate.Validator;
import net.zoostar.hc.validate.ValidatorException;

@Getter
@Service
public class UserServiceImpl extends AbstractCrudServiceImpl<User>
implements UserService {
	
	@Autowired
	protected UserRepository repository;
	
	@Override
	protected void init() throws Exception {
	}

	@Override
	protected User retrieveByKey(User user) throws MissingEntityException {
		return retrieveByEmail(user.getEmail());
	}

	@Override
	public User retrieveByEmail(String email) throws MissingEntityException {
		log.info("Retrieving user by email: {}...", email);
		var optional = getRepository().findByEmail(email);
		if(optional.isEmpty()) {
			throw new MissingEntityException("No User found with email: " + email);
		}
		return optional.get();
	}

	@Override
	protected void preCreateValidation(User user) throws ValidatorException {
		super.preCreateValidation(user);
		businessKeyValidator.validate(user);
	}

	@Override
	protected void preUpdateValidation(User user) throws ValidatorException {
		super.preUpdateValidation(user);
		businessKeyValidator.validate(user);
	}

	protected Validator<User> businessKeyValidator() {
		return new Validator<>() {

			@Override
			public void validate(User user) throws MissingRequiredFieldException {
				log.info("Validating business key for: {}...", user);
				if(StringUtils.isBlank(user.getEmail())) {
					throw new MissingRequiredFieldException("Missing Required Field: email");
				}
				if(StringUtils.isBlank(user.getSource())) {
					throw new MissingRequiredFieldException("Missing Required Field: source");
				}
			}

		};
	}

}
