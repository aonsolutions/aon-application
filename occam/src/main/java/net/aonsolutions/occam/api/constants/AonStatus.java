package net.aonsolutions.occam.api.constants;

import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonStatus {

	NOT_BILLABLE("No facturable")
		{ @Override public <R,T> R visit(AonStatusVisitor<R,T> v, T t) { return v.visitNonBillable(t);} }
	,BILLABLE("Facturable")
		{ @Override public <R,T> R visit(AonStatusVisitor<R,T> v, T t) { return v.visitBillable(t);} }
	;

	private String name;
	private AonStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static Optional<AonStatus> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<AonStatus> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonStatus.values().length) return Optional.empty();
		return Optional.of( AonStatus.values()[i]);
	}
	
	public static Optional<AonStatus> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(AonStatusVisitor<R,T> visitor, T t);
	public static interface AonStatusVisitor<R,T> {
		R visitNonBillable(T t);
		R visitBillable(T t);
	}

}
