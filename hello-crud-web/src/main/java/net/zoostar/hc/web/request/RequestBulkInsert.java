package net.zoostar.hc.web.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.zoostar.hc.model.AbstractStringPersistable;

@Getter
@Setter
@ToString
public class RequestBulkInsert<T extends AbstractStringPersistable> {
	
	private String source;
	
	private Long readerPageSize;
	
	private String readerSelectClause;
	
	private String readerFromClause;
	
	private String readerSortKey;
	
	private String mappedClass;
	
}
