package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum InvoiceCommunicationType implements Serializable{
 
	SII { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) { visitor.visitSII();}},
	TBAI { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) { visitor.visitTBAI();}},
	LROE { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitLROE();}},
	SERES { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) { visitor.visitSERES();}},
	EMAIL { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) { visitor.visitEMAIL();}},
	CLOSING { @Override public void visit(IInvoiceCommunicationTypeVisitor visitor) { visitor.visitCLOSING();}}
	;

	
	
	private InvoiceCommunicationType() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public void visit(IInvoiceCommunicationTypeVisitor visitor) throws Exception {
		// Redefine
	}
	
	public static InvoiceCommunicationType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceCommunicationType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceCommunicationType.values().length) return null;
		return InvoiceCommunicationType.values()[i];
	}
	
	public static InvoiceCommunicationType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceCommunicationType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public boolean isTbai() {
		return TBAI.equals(this);
	}
	
	public boolean isSii() {
		return SII.equals(this);
	}
	
	public boolean isLroe() {
		return LROE.equals(this);
	}
	
	public boolean isSeres() {
		return SERES.equals(this);
	}
}
