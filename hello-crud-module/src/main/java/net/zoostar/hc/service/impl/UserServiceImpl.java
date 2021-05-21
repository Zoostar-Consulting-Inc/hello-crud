package net.zoostar.hc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@Getter
@Service
public class UserServiceImpl extends AbstractCrudServiceImpl<User>
		implements UserService {

	@Autowired
	protected UserRepository repository;
	
	protected Validator<User> emailValidator = emailValidator();
	
	@Override
	public Page<User> retrieve(int number, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User update(User entity) throws ValidatorException, MissingEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User delete(String id) throws MissingEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected User persist(User user) throws DuplicateEntityException {
		if(getRepository().findByEmail(user.getEmail()).isPresent()) {
			throw new DuplicateEntityException();
		}
		
		return repository.save(user);
	}

	@Override
	protected void preCreateValidation(User user) throws ValidatorException {
		emailValidator.validate(user);
	}

	protected Validator<User> emailValidator() {
		return new Validator<User>() {

			@Override
			public void validate(User user) throws ValidatorException {
				if(user == null || StringUtils.isBlank(user.getEmail())) {
					throw new MissingRequiredFieldException("Missing Required Field: email");
				}
				log.info("Passed User Email Validation: {}", user);
			}

		};
	}

}
