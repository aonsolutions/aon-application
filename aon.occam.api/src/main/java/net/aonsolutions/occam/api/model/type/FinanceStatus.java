package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FinanceStatus implements Serializable {

	PENDING("Pendiente"){ @Override public <T> T visit(FinanceStatusVisitor<T> visitor) { return visitor.visitPending();} },
	BATCHED("Remesado" ){ @Override public <T> T visit(FinanceStatusVisitor<T> visitor) { return visitor.visitBatched();} },
	RETURNED("Devuelto"){ @Override public <T> T visit(FinanceStatusVisitor<T> visitor) { return visitor.visitReturned();} },
	PAID("Pagado"      ){ @Override public <T> T visit(FinanceStatusVisitor<T> visitor) { return visitor.visitPaid();} },
	SETTLED("Saldado"  ){ @Override public <T> T visit(FinanceStatusVisitor<T> visitor) { return visitor.visitSettled();} },
	;

	private String description;
	
	private FinanceStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<FinanceStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<FinanceStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= FinanceStatus.values().length) return Optional.empty();
		return Optional.of(FinanceStatus.values()[i]);
	}
	
	public static Optional<FinanceStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public abstract <T> T visit(FinanceStatusVisitor<T> visitor);
	public static interface FinanceStatusVisitor<T> {
		T visitPending();
		T visitBatched();
		T visitReturned();
		T visitPaid();
		T visitSettled();
	}

}