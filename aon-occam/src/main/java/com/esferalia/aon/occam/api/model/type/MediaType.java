package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MediaType implements Serializable {

	 UNKNOWN		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitUnknown();}}
	,FIXED_PHONE 	{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitFixedPhone();}}
	,CELLULAR 		{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitCellular();}}
	,FAX 			{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitFax();}}
	,EMAIL 			{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitEmail();}}
	,WEB 			{@Override public void visit(IMediaTypeVisitor visitor) { visitor.visitWeb();}}
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	public abstract void visit( IMediaTypeVisitor visitor );
	
	public static MediaType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static MediaType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DocumentType.values().length) return null;
		return MediaType.values()[i];
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