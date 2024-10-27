package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FinanceTrackingType {
	BATCHED("Remesado", FinanceStatus.BATCHED) 
		{ @Override public <T> T visit(FinanceTrackingTypeVisitor<T> visitor) { return visitor.visitBatched();} },
	PAID("Pagado", FinanceStatus.PAID )
		{ @Override public <T> T visit(FinanceTrackingTypeVisitor<T> visitor) { return visitor.visitPaid();} },
	RETURNED("Devuelto", FinanceStatus.RETURNED)
		{ @Override public <T> T visit(FinanceTrackingTypeVisitor<T> visitor) { return visitor.visitReturned();} },
    FRACTIONED("Fraccionado", FinanceStatus.PENDING)
    	{ @Override public <T> T visit(FinanceTrackingTypeVisitor<T> visitor) { return visitor.visitFractioned();} },
    SETTLED("Saldado", FinanceStatus.SETTLED)
		{ @Override public <T> T visit(FinanceTrackingTypeVisitor<T> visitor) { return visitor.visitSettled();} },
    ;
	
	private String description;
	private FinanceStatus financeStatus;
	
	private FinanceTrackingType(String description, FinanceStatus financeStatus) {
		this.description = description;
		this.financeStatus = financeStatus;
	}
	
	public String getDescription() {
		return description;
	}
	public FinanceStatus getFinanceStatus() {
		return this.financeStatus;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}


	public static Optional<FinanceTrackingType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<FinanceTrackingType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= FinanceTrackingType.values().length) return Optional.empty();
		return Optional.of(FinanceTrackingType.values()[i]);
	}
	
	public static Optional<FinanceTrackingType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public abstract <T> T visit(FinanceTrackingTypeVisitor<T> visitor);
	public static interface FinanceTrackingTypeVisitor<T> {
		T visitBatched();
		T visitPaid();
		T visitReturned();
		T visitFractioned();
		T visitSettled();
	}
}