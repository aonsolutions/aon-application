package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AccountPeriodStatus implements Serializable {

	 ACTIVE		("Activo")
	 	{ @Override public <T> T visit(AccountPeriodStatusVisitor<T> visitor) { return visitor.visitActive();} }
	,INACTIVE	("Inactivo")
		{ @Override public <T> T visit(AccountPeriodStatusVisitor<T> visitor) { return visitor.visitInactive();} }
	,OPENING	("Apertura")
		{ @Override public <T> T visit(AccountPeriodStatusVisitor<T> visitor) { return visitor.visitOpening();} }
	,OPERATING	("Explotaci\u00F3n")
		{ @Override public <T> T visit(AccountPeriodStatusVisitor<T> visitor) { return visitor.visitOperating();} }
	,CLOSED		("Cerrado")
		{ @Override public <T> T visit(AccountPeriodStatusVisitor<T> visitor) { return visitor.visitClosed();} }
 	;

	private String description;
	
	private AccountPeriodStatus(String description) {
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


	public static Optional<AccountPeriodStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AccountPeriodStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AccountPeriodStatus.values().length) return Optional.empty();
		return Optional.of(AccountPeriodStatus.values()[i]);
	}
	
	public static Optional<AccountPeriodStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	

	public abstract <T> T visit(AccountPeriodStatusVisitor<T> visitor);
	public static interface AccountPeriodStatusVisitor<T> {
		T visitActive();
		T visitInactive();
		T visitOpening();
		T visitOperating();
		T visitClosed();
	}
}
