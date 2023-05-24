package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum AccountingPeriodStatus implements Serializable {

	 ACTIVE		("Activo")
	 	{ @Override public <R,T> R visit(AccountingPeriodStatusVisitor<R,T> v, T t) { return v.visitActive(t);} }
	,INACTIVE	("Inactivo")
 		{ @Override public <R,T> R visit(AccountingPeriodStatusVisitor<R,T> v, T t) { return v.visitInactive(t);} }
	,OPENING	("Apertura")
		{ @Override public <R,T> R visit(AccountingPeriodStatusVisitor<R,T> v, T t) { return v.visitOpening(t);} }
	,OPERATING	("Explotaci\u00F3n")
		{ @Override public <R,T> R visit(AccountingPeriodStatusVisitor<R,T> v, T t) { return v.visitOperating(t);} }
	,CLOSED		("Cerrado")
		{ @Override public <R,T> R visit(AccountingPeriodStatusVisitor<R,T> v, T t) { return v.visitClosed(t);} }
 	;

	private String description;
	
	private AccountingPeriodStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return (this == ACTIVE || this == OPENING);
	}

	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<AccountingPeriodStatus> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<AccountingPeriodStatus> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AccountingPeriodStatus.values().length) return Optional.empty();
		return Optional.of( AccountingPeriodStatus.values()[i]);
	}
	
	public static Optional<AccountingPeriodStatus> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}

	public abstract <R,T> R visit(AccountingPeriodStatusVisitor<R,T> visitor, T t);
	public static interface AccountingPeriodStatusVisitor<R,T> {
		 R visitActive( T t );
		 R visitInactive( T t );
		 R visitOpening( T t );
		 R visitOperating( T t );
		 R visitClosed( T t );
	}
	
}