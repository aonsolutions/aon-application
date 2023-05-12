package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum DocumentType implements Serializable {
	
	 NIF("DNI")			
	 	{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitNif(t);} }
	,CIF("CIF")
 		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitCif(t);} }
	,NIE("NIE")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitNie(t);} }
	,PASSPORT("Pasp.")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitPassport(t);} }
	,WORK_PERMIT("P.T.")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitWorkPermit(t);} }
	,COMMUNITY_CARD("T.C.")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitCommunityCard(t);} }
	,OTHER("Otr.")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitOther(t);} }
	,NOT_CENSUSED("No Censado")
		{ @Override public <R,T> R visit(DocumentTypeVisitor<R,T> v, T t) { return v.visitNotCensused(t);} }
	;

	private String description;
	
	private DocumentType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	public static Optional<DocumentType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( (int) i);
	}
	
	public static Optional<DocumentType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= DocumentType.values().length) return Optional.empty();
		return Optional.of( DocumentType.values()[i] );
	}
	
	public static Optional<DocumentType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(DocumentTypeVisitor<R,T> visitor, T t);
	public static interface DocumentTypeVisitor<R,T> {
		 R visitNif( T t );
		 R visitCif( T t );
		 R visitNie( T t );
		 R visitPassport( T t );
		 R visitWorkPermit( T t );
		 R visitCommunityCard( T t );
		 R visitOther( T t );
		 R visitNotCensused( T t );
	}

}
