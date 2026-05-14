package com.esferalia.aon.occam.api.model.console;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleSchema {
	
	 PRO			("PRO"		,"pro-aonsolutions-net","console-pro.aonsolutions.net","jgarcia")
	,AYUDAT			("AYUDAT"	,"ayudat-aonsolutions-net","console-ayudat.aonsolutions.net","jgarcia")
	,GRUPO_AYUDAT	("GRUPO"	,"grupo-ayudat-aonsolutions-net","console-grupoayudat.aonsolutions.net","jgarcia")
	,TOLEDO_ASESORES("TOLEDO"	,"pro-toledoasesores-es","console-tya.aonsolutions.net","jgarcia")
	,AYG			("AYG"		,"ayg-toledoasesores-es","console-ayg.aonsolutions.net","jgarcia")
	,CASTELLANA		("CASTELL"	,"castellana-toledoasesores-es","console-castellana.aonsolutions.net","jgarcia")
	,ZARATE			("ZARATE"	,"zar-aonsolutions-net","console-zar.aonsolutions.net","jgarcia")
	,ETL			("ETL"		,"etl.aonsolutions.net","console-etl.aonsolutions.net","jgarcia")
	,UDAPA			("UDAPA"	,"grupo-udapa-aonsolutions-net","console-udapa.aonsolutions.net","jgarcia")
	,DEMO			("DEMO"		,"demo-aonsolutions-net","console-demos.aonsolutions.net","jgarcia")
	
	,SUITE_SNS		("SUITE_SNS","suite-aonsolutions-org","console.aonsolutions.org","jgarcia")
	,DEMOS_SNS		("DEMOS_SNS","demos-aonsolutions-org","console-demos.aonsolutions.org","jgarcia")
	,QA_SNS			("QA_SNS"	,"qa-aonsolutions-org","console-qa.aonsolutions.org","jgarcia")
	
	
	// -------------------------------------------
	// BASES DE DATOS DE DESARROLLO EN MI MAQUINA
	// -------------------------------------------
	// ,EUK_JOOQ_ORG	("EK_AYUDAT","aon_jooq_efd9c","console-aonsolutions.test","admin")
	// -------------------------------------------
	;
	
	private String nickName;
	private String schema; 
	private String domainName;
	private String user;
	
	private ConsoleSchema(String nickName, String schema, String domainName, String user ) {
		this.nickName = nickName;
		this.schema = schema; 
		this.domainName = domainName;
		this.user = user;
	}
	public String getNickName() {
		return nickName;
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
	
	public static Optional<ConsoleSchema> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return AonCollectionUtils.stream( ConsoleSchema.values() )
			.filter(rs -> i.equalsIgnoreCase(rs.name())
				|| i.equalsIgnoreCase(rs.getNickName())
				|| i.equalsIgnoreCase(rs.getSchema())
				|| i.equalsIgnoreCase(rs.getDomainName()))
			.findFirst()
		;
	}
	
	public static ConsoleSchema[] valuess() {
		return values();
	}
}
