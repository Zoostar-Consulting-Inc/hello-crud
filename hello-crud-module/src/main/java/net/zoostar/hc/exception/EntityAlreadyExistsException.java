package net.zoostar.hc.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final transient Object entity;
	
	public EntityAlreadyExistsException(Object entity) {
		super("Entity already exists!");
		this.entity = entity;
	}
}
