package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FiscalStatus implements Serializable {
	 
	PENDING("Pendiente")	{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor){ return visitor.visitPending();} },
	FINISHED("Finalizado")	{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitFinished();} },
	BATCHED("En Lote")		{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitBatched();} },
	BLOCKED("Bloqueado")	{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitBlocked();} },
	SENT("Presentado")		{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitSent();} },
	MISSING("Desconocido")	{ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitMissing();} },
	CUSTOMER_CHECK("Envio a cliente"){ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitCustomerCheck();} },
	CUSTOMER_ACCEPTED("Aceptado por cliente"){ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitCustomerAccepted();} },
	CUSTOMER_REJECTED("Rechazado por cliente"){ @Override public <T> T visit(IFiscalStatusVisitor<T> visitor) { return visitor.visitCustomerRejected();} },
	;

	private String name;
	
	private FiscalStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) ordinal();
	}

	public abstract <T> T visit(IFiscalStatusVisitor<T> visitor);
	
	public static FiscalStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		return FiscalStatus.valueOf( i );
	}
	public static FiscalStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= FiscalStatus.values().length) return null;
		return FiscalStatus.values()[i];
	}
	
}
