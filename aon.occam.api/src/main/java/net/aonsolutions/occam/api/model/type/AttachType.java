package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AttachType  implements Serializable {
	
	REGISTRY("registry")
	,CONTRACT("contract")
	,ITEM("item")
	,INVOICE("invoice")
	,OFFER("offer")
	,PAYROLL("payroll")
	,PROJECT("project")
	,SEPE("sepe")
	,MOD111("mod111")
	,MOD115("mod115")
	,MOD123("mod123")
	,PAYSHEET("paysheet")
	,DATA("data")
	,RAWDOC("rawdoc")
	,TASK("task");
	
	private String name;
	
	private AttachType(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public byte value(){
		return (byte) ordinal();
	}
	

	public static Optional<AttachType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AttachType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AttachType.values().length) return Optional.empty();
		return Optional.of(AttachType.values()[i]);
	}
	
	public static Optional<AttachType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(AttachType t) {
		return t == null ? null : t.value();
	}
}
