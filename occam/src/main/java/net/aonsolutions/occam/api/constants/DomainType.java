package net.aonsolutions.occam.api.constants;

import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum DomainType {

	ENTERPRISE("Empresa")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitEnterprise(t);} }
	,CONSULTANCY("Asesor\u00EDa")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitConsultancy(t);} }
	,GARAGE("Garaje")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitGarage(t);} }
	,ACADEMY("Academ\u00EDa")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitAcademy(t);} }
	,HOTEL("Hotel")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitHotel(t);} }
	,ADMIN("Administraci\u00F3n")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitAdmin(t);} }
	,OFFICE("Despacho")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitOffice(t);} }
	,GENERIC("Gen\u00E9rico")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitGeneric(t);} }
	,COMMERCE("Comercio")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitCommerce(t);} }
	,KIT_DIGITAL("Kit Digital")
		{ @Override public <R,T> R visit(DomainTypeVisitor<R,T> v, T t) { return v.visitKitDigital(t);} }
	;
	private String name;
	private DomainType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<DomainType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<DomainType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= DomainType.values().length) return Optional.empty();
		return Optional.of( DomainType.values()[i]);
	}
	
	public static Optional<DomainType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.toString()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(DomainTypeVisitor<R,T> visitor, T t);
	public static interface DomainTypeVisitor<R,T> {
		R visitEnterprise(T t);
		R visitConsultancy(T t);
		R visitGarage(T t);
		R visitAcademy(T t);
		R visitHotel(T t);
		R visitAdmin(T t);
		R visitOffice(T t);
		R visitGeneric(T t);
		R visitCommerce(T t);
		R visitKitDigital(T t);
	}

}
