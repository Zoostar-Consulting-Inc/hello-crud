package net.zoostar.hc.validate;

public interface Validator<T> {
	void validate(T object) throws ValidatorException;
}
