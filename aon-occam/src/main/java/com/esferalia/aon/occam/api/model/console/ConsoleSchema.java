package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleSchema {
	
	 PRO			("pro-aonsolutions-net","console-pro.aonsolutions.net","jgarcia")
	,AYUDAT			("ayudat-aonsolutions-net","console-ayudat.aonsolutions.net","jgarcia")
	,GRUPO_AYUDAT	("grupo-ayudat-aonsolutions-net","console-grupoayudat.aonsolutions.net","jgarcia")
	,TOLEDO_ASESORES("pro-toledoasesores-es","console-tya.aonsolutions.net","jgarcia")
	,AYG			("ayg-toledoasesores-es","console-ayg.aonsolutions.net","jgarcia")
	,CASTELLANA		("castellana-toledoasesores-es","console-castellana.aonsolutions.net","jgarcia")
	,ZARATE			("zar-aonsolutions-net","console-zar.aonsolutions.net","jgarcia")
	,ETL			("etl.aonsolutions.net","console-etl.aonsolutions.net","jgarcia")
	,UDAPA			("grupo-udapa-aonsolutions-net","console-udapa.aonsolutions.net","jgarcia")
	,DEMO			("demo-aonsolutions-net","console-demos.aonsolutions.net","jgarcia")
	;
	
	private String schema; 
	private String domainName;
	private String user;
	
	private ConsoleSchema(String schema, String domainName, String user ) {
		this.schema = schema; 
		this.domainName = domainName;
		this.user = user;
	}
	
	public String getSchema() {
		return schema;
	}
	public String getDomainName() {
		return domainName;
	}
	public String getUser() {
		return user;
	}
	
	public static ConsoleSchema safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleSchema rs : values()) {
			if(i.equalsIgnoreCase(rs.name())
				|| i.equalsIgnoreCase(rs.getSchema())
				|| i.equalsIgnoreCase(rs.getDomainName()))
				return rs;
		}
		return null;
	}
}
