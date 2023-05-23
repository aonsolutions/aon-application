package net.aonsolutions.occam.api.constants;

import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum AonModule {

	MARKETING( "marketing" ),
	CRM( "crm" ),
	MANAGEMENT( "management" ),
	TREASURY( "treasury" ),
	WAREHOUSE( "warehouse" ),
	GROUPWARE( "groupware" ),
	ACCOUNTING( "accounting" ),
	FISCAL( "fiscal" ),
	PAYROLL( "payroll" ),
	DOCUMENT( "document" ),
	GARAGE( "garage" ),
	ACADEMY( "academy" ),
	HOTEL( "hotel" ),
	INFOWEB( "infoweb" ),
	PAYROLL_PORTAL( "payroll_portal" ),
	DOCUMENT_PORTAL( "document_portal" ),
	POS( "pos" ),
	COMUNICA( "comunica" ),
	CONFIGURATION( "configuration" ),
	AON_ONE( "aonOne" ),
	ECOMMERCE( "eCommerce" ),
	CALL_CENTER( "call_center" ),
	FINANCE_PORTAL( "finance_portal" ),
	AON_FINANCE( "aonFinance" ),
	SUITE_PORTAL("suite_portal");
    
	private String name;
	
	private AonModule(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<AonModule> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<AonModule> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonModule.values().length) return Optional.empty();
		return Optional.of( AonModule.values()[i]);
	}
	
	public static Optional<AonModule> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	
}