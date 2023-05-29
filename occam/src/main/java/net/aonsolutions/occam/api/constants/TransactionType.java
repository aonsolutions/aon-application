package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum TransactionType implements Serializable {

	NATIONAL ("Nacional", "NAC")
		{ @Override public <R,T> R visit(TransactionTypeVisitor<R,T> v, T t) { return v.visitNational(t);} }
	,INTRACOMMUNITY("Intracomunitaria", "INTR")
		{ @Override public <R,T> R visit(TransactionTypeVisitor<R,T> v, T t) { return v.visitIntracommunity(t);} }
	,EXTRACOMMUNITY("Extracomunitaria", "EXTR")
		{ @Override public <R,T> R visit(TransactionTypeVisitor<R,T> v, T t) { return v.visitExtracommunity(t);} }
	,CAN_CEU_MEL("Canarias, Ceuta y Melilla", "CCM")
		{ @Override public <R,T> R visit(TransactionTypeVisitor<R,T> v, T t) { return v.visitCanCeuMel(t);} }
	,OTHER_ISP("I.S.P.", "ISP")
		{ @Override public <R,T> R visit(TransactionTypeVisitor<R,T> v, T t) { return v.visitOtherISP(t);} }
	;
	
	private String description;
	private String abbreviatedDescription;
	
	private TransactionType(String description, String abbreviatedDescription) {
		this.description = description;
		this.abbreviatedDescription = abbreviatedDescription;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAbbreviatedDescription() {
		return abbreviatedDescription;
	}

	public static Optional<TransactionType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( (int) i);
	}
	
	public static Optional<TransactionType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= TransactionType.values().length) return Optional.empty();
		return Optional.of( TransactionType.values()[i] );
	}
	
	public static Optional<TransactionType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(TransactionTypeVisitor<R,T> visitor, T t);
	public static interface TransactionTypeVisitor<R,T> {
		 R visitNational( T t );
		 R visitIntracommunity( T t );
		 R visitExtracommunity( T t );
		 R visitCanCeuMel( T t );
		 R visitOtherISP( T t );
	}
}