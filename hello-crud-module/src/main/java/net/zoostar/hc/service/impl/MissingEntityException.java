package net.zoostar.hc.service.impl;

import net.zoostar.hc.validate.ValidatorException;

public class MissingEntityException extends ValidatorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingEntityException(String msg) {
		super(msg);
	}

}
