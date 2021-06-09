package net.zoostar.hc.service.impl;

import net.zoostar.hc.validate.ValidatorException;

public class DuplicateEntityException extends ValidatorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DuplicateEntityException(String message) {
		super(message);
	}

}
