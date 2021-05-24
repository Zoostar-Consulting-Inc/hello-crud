package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.zoostar.hc.dao.UserRepository;
import net.zoostar.hc.model.User;
import net.zoostar.hc.service.UserService;
import net.zoostar.hc.validate.MissingRequiredFieldException;
import net.zoostar.hc.validate.Validator;
import net.zoostar.hc.validate.ValidatorException;

@Slf4j
@Service
public class UserServiceImpl extends AbstractCrudServiceImpl<User>
		implements UserService {

	@Getter
	@Autowired
	protected UserRepository repository;
	
	protected final Validator<User> businessKeyValidator = businessKeyValidator();

	@Override
	protected User persist(User user) throws DuplicateEntityException {
		if(getRepository().findByEmail(user.getEmail()).isPresent()) {
			throw new DuplicateEntityException();
		}
		
		return repository.save(user);
	}

	@Override
	protected void preCreateValidation(User user) throws ValidatorException {
		businessKeyValidator.validate(user);
	}

	protected Validator<User> businessKeyValidator() {
		return new Validator<User>() {

			@Override
			public void validate(User user) throws ValidatorException {
				if(user == null || StringUtils.isBlank(user.getEmail())) {
					throw new MissingRequiredFieldException("Missing Required Field: email");
				}
				if(StringUtils.isBlank(user.getSource())) {
					throw new MissingRequiredFieldException("Missing Required Field: source");
				}
				log.info("Passed Business Key Validation: {}", user);
			}

		};
	}

}
