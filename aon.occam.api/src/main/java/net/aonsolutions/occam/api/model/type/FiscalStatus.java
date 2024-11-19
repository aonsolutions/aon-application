package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

public enum FiscalStatus implements Serializable {
	 
	PENDING("Pendiente")	{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor){ return visitor.visitPending();} },
	FINISHED("Finalizado")	{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitFinished();} },
	BATCHED("En Lote")		{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitBatched();} },
	BLOCKED("Bloqueado")	{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitBlocked();} },
	SENT("Presentado")		{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitSent();} },
	MISSING("Desconocido")	{ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitMissing();} },
	CUSTOMER_CHECK("Envio a cliente"){ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitCustomerCheck();} },
	CUSTOMER_ACCEPTED("Aceptado por cliente"){ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitCustomerAccepted();} },
	CUSTOMER_REJECTED("Rechazado por cliente"){ @Override public <T> T visit(FiscalStatusVisitor<T> visitor) { return visitor.visitCustomerRejected();} },
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

	public static Optional<FiscalStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<FiscalStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= FiscalStatus.values().length) return Optional.empty();
		return Optional.of(FiscalStatus.values()[i]);
	}

	public abstract <T> T visit(FiscalStatusVisitor<T> visitor);
	public static interface FiscalStatusVisitor<T> {
		T visitPending();
		T visitFinished();
		T visitBatched();
		T visitBlocked();
		T visitSent();
		T visitMissing();
		T visitCustomerCheck();
		T visitCustomerAccepted();
		T visitCustomerRejected();
	}
	
}
