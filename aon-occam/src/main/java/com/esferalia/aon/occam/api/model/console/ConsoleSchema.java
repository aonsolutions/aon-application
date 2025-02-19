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
	
	// -------------------------------------------
	// BASES DE DATOS DE DESARROLLO EN MI MÒAQUINA
	// -------------------------------------------
	,EUK_AYUDAT		("EK_AYUDAT","ayudat-aonsolutions-net","console-ayudat.ecastellano.euk","jgarcia")
	,EUK_GRUPO		("EK_GRUPO"	,"grupo-ayudat-aonsolutions-net","console-grupoayudat.ecastellano.euk","jgarcia")
	,EUK_PRO_EUK	("EK_PROEUK","pro-aonsolutions-euk","console-pro.ecastellano.pro","jgarcia")
	,EUK_PRO_NET	("EK_PRO"	,"pro-aonsolutions-net","console-pro.ecastellano.euk","jgarcia")
	,EUK_SIG		("EK_SUITE"	,"suite-aonsolutions-org","console.ecastellano.org","jgarcia")
	,EUK_TEST		("EK_TEST"	,"test-aonsolutions-org","admin-test.aonsolutions.org","jgarcia")
	,EUK_ZAR		("EK_ZARA"	,"zar-aonsolutions-net","console-zar.ecastellano.pro","jgarcia")
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
	
	public static void main(String[] args) {
		ConsoleSchema.safeValueOf( "ayudat-aonsolutions-net")	
			.ifPresentOrElse(cs -> System.out.println( cs )
					,() -> System.out.println( "NADA" ) );
			
	}
}
