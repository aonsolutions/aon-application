package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MediaType implements Serializable {

	 UNKNOWN( "-----" )
	 	{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitUnknown();}}
	,FIXED_PHONE("Tel\u00E9fono")
		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitFixedPhone();}}
	,CELLULAR("M\u00F3vil")
		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitCellular();}}
	,FAX("Fax")
		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitFax();}}
	,EMAIL("eMail")
		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitEmail();}}
	,WEB("Web")
		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitWeb();}}
	;
	
	private String description;
	
	private MediaType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	public abstract void visit( IMediaTypeVisitor visitor );
	
	public static MediaType safeValueOf( Byte i ) {
		if (i == null) return UNKNOWN;
		return safeValueOf( i.intValue() ); 
	}
	
	public static MediaType safeValueOf( Integer i ) {
		if (i == null) return UNKNOWN;
		if (i < 0 || i >= DocumentType.values().length) return UNKNOWN;
		return MediaType.values()[i];
	}
	
	public static MediaType safeValueOf( String i ) {
		for (MediaType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return UNKNOWN;
	}
	
	
	public static interface IMediaTypeVisitor {
		void visitUnknown();
		void visitFixedPhone();
		void visitCellular();
		void visitFax();
		void visitEmail();
		void visitWeb();
	}
	
}