package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.mutable.MutableObject;

public enum DataRequestType implements Serializable{

	SII,
	TBAI,
	LROE,
	VERIFACTU,
	SERES,
	NO_VERIFACTU,
	SIF,
	FACTURAE 
	;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static DataRequestType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static DataRequestType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= DataRequestType.values().length)
			return null;
		return DataRequestType.values()[i];
	}
	
	public static DataRequestType safeValueOf(InvoiceCommunicationType ict) {
		if ( ict == null) return null;
		MutableObject<DataRequestType> ret = new MutableObject<>();
		try {
			ict.visit( new InvoiceCommunicationTypeVisitor() {
				@Override public void visitVERIFACTU() throws InvoiceCommunicationException { ret.setValue( DataRequestType.VERIFACTU );}
				@Override public void visitTBAI() throws InvoiceCommunicationException {ret.setValue( DataRequestType.TBAI );}
				@Override public void visitSII() throws InvoiceCommunicationException {ret.setValue( DataRequestType.SII );}
				@Override public void visitSIF() throws InvoiceCommunicationException {ret.setValue( DataRequestType.SIF );}
				@Override public void visitSERES() throws InvoiceCommunicationException {ret.setValue( DataRequestType.SERES );}
				@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException {ret.setValue( DataRequestType.NO_VERIFACTU );}
				@Override public void visitLROE() throws InvoiceCommunicationException {ret.setValue( DataRequestType.LROE );}
				@Override public void visitFACTURAE() throws InvoiceCommunicationException {ret.setValue( DataRequestType.FACTURAE );}
				@Override public void visitEMAIL() throws InvoiceCommunicationException {/* Nothing */}
				@Override public void visitCLOSING() throws InvoiceCommunicationException {/* Nothing */}
			});
		} catch (Exception e) {
			/* Nothing */
		}
		return ret.getValue();
	}
}