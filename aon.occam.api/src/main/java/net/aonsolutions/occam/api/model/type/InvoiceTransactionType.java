package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceTransactionType implements Serializable {

	NATIONAL ("Nacional", "NAC")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor) { return visitor.visitNational();} },
	INTRACOMMUNITY("Intracomunitaria", "INTR")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor) { return visitor.visitIntracommunity();} },
	EXTRACOMMUNITY("Extracomunitaria", "EXTR")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor) { return visitor.visitExtracommunity();} },
	CAN_CEU_MEL("Canarias, Ceuta y Melilla", "CCM")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor) { return visitor.visitCanCeuMel();} },
	OTHER_ISP("I.S.P.", "ISP")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor) { return visitor.visitOtherISP();} },
	;
	
	private String description;
	private String shortName;
	
	private InvoiceTransactionType(String description, String shortName) {
		this.description = description;
		this.shortName = shortName;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getShortName() {
		return shortName;
	}

	public static Optional<InvoiceTransactionType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceTransactionType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceTransactionType.values().length) return Optional.empty();
		return Optional.of(InvoiceTransactionType.values()[i]);
	}
	
	public static Optional<InvoiceTransactionType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s)
				|| AonStringUtils.equalsIgnoreCase(t.getShortName(), s)
				|| AonStringUtils.equalsIgnoreCase(t.getDescription(), s))
			.findFirst();
	}
	
	public static Byte value(InvoiceTransactionType t) {
		return t == null ? null : t.value();
	}
	public static String name(InvoiceTransactionType t) {
		return t == null ? null : t.name();
	}

	public abstract <T> T visit(InvoiceTransactionTypeVisitor<T> visitor);
	public static interface InvoiceTransactionTypeVisitor<T> {
		T visitNational();
		T visitIntracommunity();
		T visitExtracommunity();
		T visitCanCeuMel();
		T visitOtherISP();
	}

}