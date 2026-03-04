package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.mutable.MutableObject;


public enum DataAttachSource implements Serializable {
	
	QUALITY,
	INVOICE,
	FBATCH,
	PRODUCTION,
	DELIVERY,
	SII,
	SERES,
	INGENET,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	IMPORTATION,
	SISTEMA_RED,
	INVOICE_PRINT_CONFIGURATION,
	TBAI,
	MOD202,
	MOD190,
	LROE,
	MOD180,
	MOD193,
	MOD184,
	MOD347,
	MOD349,
	MOD200,
	MOD369,
	VERIFACTU,
	SIF,
	NO_VERIFACTU, 
	FACTURAE,
	MOD421
	;

	public byte value() {
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static DataAttachSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static DataAttachSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DataAttachSource.values().length) return null;
		return DataAttachSource.values()[i];
	}

	public static Byte value(DataAttachSource s) {
		if (s == null) return null;
		return s.value();
	}
	
	public static Byte safeByteOf(InvoiceCommunicationType ict) {
		return value( safeValueOf(ict) );
	}
	
	public static DataAttachSource safeValueOf(InvoiceCommunicationType ict) {
		if ( ict == null) return null;
		MutableObject<DataAttachSource> ret = new MutableObject<>();
		try {
			ict.visit( new InvoiceCommunicationTypeVisitor() {
				@Override public void visitVERIFACTU() throws InvoiceCommunicationException { ret.setValue( DataAttachSource.VERIFACTU );}
				@Override public void visitTBAI() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.TBAI );}
				@Override public void visitSII() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.SII );}
				@Override public void visitSIF() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.SIF );}
				@Override public void visitSERES() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.SERES );}
				@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.NO_VERIFACTU );}
				@Override public void visitLROE() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.LROE );}
				@Override public void visitFACTURAE() throws InvoiceCommunicationException {ret.setValue( DataAttachSource.FACTURAE );			}
				@Override public void visitEMAIL() throws InvoiceCommunicationException {/* Nothing */}
				@Override public void visitCLOSING() throws InvoiceCommunicationException {/* Nothing */}
			});
		} catch (Exception e) {
			/* Nothing */
		}
		return ret.getValue();
	}
	
}