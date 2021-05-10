package net.zoostar.hc.model;

import org.springframework.data.domain.PageImpl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content<T> {
	PageImpl<T> content;
}
