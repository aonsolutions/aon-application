package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum InvoiceCommunicationType implements Serializable {
 
	SII { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSII();}},
	TBAI { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitTBAI();}},
	LROE { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitLROE();}},
	SERES { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSERES();}},
	EMAIL { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitEMAIL();}},
	CLOSING { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitCLOSING();}},
	VERIFACTU { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitVERIFACTU();}},
	NO_VERIFACTU{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitNO_VERIFACTU();}},
	SIF{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSIF();}},
	FACTURAE { @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitFACTURAE();}}
	;
	
	private InvoiceCommunicationType() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public Optional<Administration> defaultAdministration() {
		if ( this == VERIFACTU || this == SII) return Optional.of( Administration.COMMON_TERRITORY );
		if ( this == LROE ) return Optional.of( Administration.BIZKAIA );
		return Optional.empty();
	}
	
	public static String name( InvoiceCommunicationType i ) {
		return (i == null) ? null : i.name(); 
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
	
	public boolean isVerifactu() {
		return VERIFACTU.equals(this);
	}
	
	public boolean mustApplyToInputInvoices() {
		MutableBoolean ret = new MutableBoolean(false);
		try {
			visit(new InvoiceCommunicationTypeVisitor() {
				@Override public void visitSII() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitTBAI() throws InvoiceCommunicationException {ret.setValue(false);}
				@Override public void visitLROE() throws InvoiceCommunicationException {ret.setValue(false);}
				@Override public void visitSERES() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitEMAIL() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitCLOSING() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitVERIFACTU() throws InvoiceCommunicationException {ret.setValue(false);}
			});
		} catch (Exception e) {
			ret.setValue(false);
		}
		return ret.getValue();
	}
	
	public boolean mustApplyToOutputInvoices() {
		MutableBoolean ret = new MutableBoolean(false);
		try {
			visit(new InvoiceCommunicationTypeVisitor() {
				@Override public void visitSII() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitTBAI() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitLROE() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitSERES() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitEMAIL() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitCLOSING() throws InvoiceCommunicationException {ret.setValue(true);}
				@Override public void visitVERIFACTU() throws InvoiceCommunicationException {ret.setValue(true);}
			});
		} catch (Exception e) {
			ret.setValue(false);
		}
		return ret.getValue();
	}

	public abstract void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception;
	
	public static interface InvoiceCommunicationTypeVisitor {
		void visitSII() throws InvoiceCommunicationException;
		void visitTBAI() throws InvoiceCommunicationException;
		void visitLROE() throws InvoiceCommunicationException;
		void visitSERES() throws InvoiceCommunicationException;
		void visitEMAIL() throws InvoiceCommunicationException;
		void visitCLOSING() throws InvoiceCommunicationException;
		void visitVERIFACTU() throws InvoiceCommunicationException;
		void visitNO_VERIFACTU() throws InvoiceCommunicationException;
		void visitSIF() throws InvoiceCommunicationException;
		void visitFACTURAE() throws InvoiceCommunicationException;
	}
	
}
