package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum DocumentType implements Serializable {
	
	 NIF("DNI")
	 	{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitNif();}}
	,CIF("CIF")
		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitCif();}}
	,NIE("NIE")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitNie();}}
	,PASSPORT("Pasp.")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitPassport();}}
	,WORK_PERMIT("P.T.")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitWorkPermit();}}
	,COMMUNITY_CARD("T.C.")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitCommunityCard();}}
	,OTHER("Otr.")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitOther();}}
	,NOT_CENSUSED("No Cens.")
 		{@Override public <T> T visit(DocumentTypeVisitor<T> v) { return v.visitNotCensused();}}
	;

	private String shortName;
	
	private DocumentType(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<DocumentType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<DocumentType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= DocumentType.values().length) return Optional.empty();
		return Optional.of(DocumentType.values()[i]);
	}
	
	public static Optional<DocumentType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(DocumentType t) {
		return t == null ? null : t.value();
	}
	public static String name(DocumentType t) {
		return t == null ? null : t.name();
	}
	public static Optional<String> shortName(DocumentType t) {
		return Optional.ofNullable(t == null ? null : t.getShortName());
	}
	
	public abstract <T> T visit( DocumentTypeVisitor<T> visitor );
	public static interface DocumentTypeVisitor<T> {
		T visitNif();
		T visitCif();
		T visitNie();
		T visitPassport();
		T visitWorkPermit();
		T visitCommunityCard();
		T visitOther();
		T visitNotCensused();
	}
}
