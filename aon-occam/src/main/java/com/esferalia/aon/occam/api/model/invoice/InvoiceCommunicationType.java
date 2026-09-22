package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;


public enum InvoiceCommunicationType implements Serializable {
 
	SII ("S.I.I.","Suministro Inmediato de Informaci\u00F3n")
	{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSII();}},
	TBAI ("Ticket BAI","Ticket BAI")
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitTBAI();}},
	LROE ("L.R.O.E.","Libro Registro de Operaciones Econ\u00F3micas") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitLROE();}},
	SERES ("SERES","Plataforma de Intercambio Electr\u00F3nico de Documentos") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSERES();}},
	EMAIL ("Env\u00EDo Mail","Env\u00EDo por correo electr\u00F3nico") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitEMAIL();}},
	CLOSING ("Cierre","Cierre de facturaci\u00F3n") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitCLOSING();}},
	VERIFACTU ("Verifactu","Plataforma Verifactu") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitVERIFACTU();}},
	NO_VERIFACTU ("No Verifactu","Plataforma No Verifactu")
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitNO_VERIFACTU();}},
	SIF ("S.I.F.","Sistema Inform\u00E1tico de Facturaci\u00F3n")
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitSIF();}},
	FACTURAE ("FacturaE","Factura Electr\u00F3nica") 
		{ @Override public void visit(InvoiceCommunicationTypeVisitor visitor) throws Exception { visitor.visitFACTURAE();}}
	;
	
	private final String abbr;
	private final String description;
	
	private InvoiceCommunicationType(String  abbr, String description) {
		this.abbr = abbr;
		this.description = description;
	}
	
	public String getAbbr() {
		return this.abbr;
	}
	public String getDescription() {
		return this.description;
	}

	public Byte value(){
		return (byte) ordinal();
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
	
	public boolean isNoVerifactu() {
		return NO_VERIFACTU.equals(this);
	}
	
	public boolean isSif() {
		return SIF.equals(this);
	}
	
	public boolean isFacturae() {
		return FACTURAE.equals(this);
	}
	
	public boolean isEmail() {
		return EMAIL.equals(this);
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
		
		default void throwSERES() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_SERES);
		}
		default void throwEMAIL() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_EMAIL);
		}
		default void throwVERIFACTU() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_VERIFACTU);
		}
		default void throwNO_VERIFACTU() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_NO_VERIFACTU);
		}
		default void throwCLOSING() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_CLOSING);
		}
		default void throwSII() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_SII);
		}
		default void throwTBAI() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_TBAI);
		}
		default void throwLROE() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_LROE);
		}
		default void throwFACTURAE() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_FACTURAE);
		}
		default void throwSIF() throws InvoiceCommunicationException {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_NO_SIF);
		}
	}
	
}
