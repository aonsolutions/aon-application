package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.mutable.MutableObject;

public enum DataResponseSource implements Serializable{

	QUALITY,
	PROJECT,
	HOTEL,
	SII,
	SII_INVOICE,
	SII_FINANCE,
	TBAI,
	SERES_DELIVERY,
	SERES_INVOICE,
	SERES_SALES,
	INGENET,
	PACKING_LIST_NOTIFICATION,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	INGENET_SALES,
	PATURPAT_QUALITY,
	ANALYTIC_ACCOUNTING,
	TBAI_TEST,
	IMPORTATION,
	NOTIFICATION_TOKEN,
	MOD202,
	MOD190,
	LROE,
	MOD180,
	MOD193,
	MOD184,
	MOD347,
	MOD349,
	MOD200,
	LROE_TEST,
	PACKAGING_DELIVERY,
	MOD369,
	VERIFACTU,
	NO_VERIFACTU,
	SIF,
	FACTURAE,
	MOD421
	;


	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static DataResponseSource safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static DataResponseSource safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= DataResponseSource.values().length)
			return null;
		return DataResponseSource.values()[i];
	}
	public static Optional<DataResponseSource> optOf(InvoiceCommunicationType ict) {
		return Optional.ofNullable(safeValueOf(ict));
	}
	public static DataResponseSource safeValueOf(InvoiceCommunicationType ict) {
		if ( ict == null) return null;
		MutableObject<DataResponseSource> ret = new MutableObject<>();
		try {
			ict.visit( new InvoiceCommunicationTypeVisitor() {
				@Override public void visitVERIFACTU() throws InvoiceCommunicationException { ret.setValue( DataResponseSource.VERIFACTU );}
				@Override public void visitTBAI() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.TBAI );}
				@Override public void visitSII() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.SII );}
				@Override public void visitSIF() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.SIF );}
				@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.NO_VERIFACTU );}
				@Override public void visitLROE() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.LROE );}
				@Override public void visitFACTURAE() throws InvoiceCommunicationException {ret.setValue( DataResponseSource.FACTURAE );}
				@Override public void visitSERES() throws InvoiceCommunicationException {/* Nothing */}
				@Override public void visitEMAIL() throws InvoiceCommunicationException {/* Nothing */}
				@Override public void visitCLOSING() throws InvoiceCommunicationException {/* Nothing */}
			});
		} catch (Exception e) {
			/* Nothing */
		}
		return ret.getValue();
	}
}