package com.esferalia.aon.occam.api.model.console;

public enum ConsoleSchema {
	
	 PRO			("pro-aonsolutions-net","console-pro.aonsolutions.net","jgarcia")
	,ZARATE			("zar-aonsolutions-net","console-zar.aonsolutions.net","jgarcia")
	,AYUDAT			("ayudat-aonsolutions-net","console-ayudat.aonsolutions.net","jgarcia")
	,GRUPO_AYUDAT	("grupo-ayudat-aonsolutions-net","console-grupoayudat.aonsolutions.net","jgarcia")
	,ETL			("etl.aonsolutions.net","console-etl.aonsolutions.net","jgarcia")
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
	
}
