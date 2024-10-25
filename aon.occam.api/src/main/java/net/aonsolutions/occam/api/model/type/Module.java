package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Module {

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
	
	private Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<Module> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<Module> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= Module.values().length) return Optional.empty();
		return Optional.of(Module.values()[i]);
	}
	
	public static Optional<Module> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s)
						|| AonStringUtils.equalsIgnoreCase(t.getName(), s))
			.findFirst();
	}
	
}