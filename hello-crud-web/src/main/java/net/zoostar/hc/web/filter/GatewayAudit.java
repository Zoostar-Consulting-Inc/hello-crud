package net.zoostar.hc.web.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GatewayAudit {

	private String id;
	
	private long time;
	
	private long duration;
	
	private String endPoint;
	
	private String remoteAddress;

	private String username;
	
}
