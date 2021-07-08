package net.zoostar.hc.model;

public interface EntityWrapper<T extends AbstractStringPersistable> {
	T toEntity();
}
