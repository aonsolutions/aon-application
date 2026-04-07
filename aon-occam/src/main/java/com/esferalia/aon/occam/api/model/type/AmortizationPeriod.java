package com.esferalia.aon.occam.api.model.type;

import java.util.Optional;

public enum AmortizationPeriod {
	
    YEARLY		(1 ,"Anual") 		{ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitYearly(); }},
    MONTHLY		(12,"Mensual") 		{ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitMonthly(); }},
    BI_MONTHLY	(6 ,"Bimensual") 	{ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitBiMonthly(); }},
    QUARTERLY	(4 ,"Trimestral") 	{ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitQuarterly(); }},
    FOUR_MONTHLY(3 ,"Cuatrimestral"){ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitFourMonthly(); }},
    HALF_YEARLY	(2 ,"Semestral") 	{ @Override	public <T> T visit(AmortizationPeriodVisitor<T> visitor) {return visitor.visitHalfYearly(); }}
    ;
       
    private int yearFraction;
    private String description;
   
    private AmortizationPeriod(int yearFraction, String description) {
        this.yearFraction = yearFraction;
        this.description = description;
    }
   
    public double getYearFraction() {
        return yearFraction;
    }
    public String getDescription() {
		return description;
	}
	public static Optional<AmortizationPeriod> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty(); ;
		return safeValueOf( i.intValue() ); 
	}
	public static Optional<AmortizationPeriod> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty(); ;
		if (i < 0 || i >= AmortizationPeriod.values().length) return null;
		return Optional.of(AmortizationPeriod.values()[i]);
	}
	
	public abstract <T> T visit(AmortizationPeriodVisitor<T> visitor);
	
	public interface AmortizationPeriodVisitor<T> {
		T visitYearly();
		T visitMonthly();
		T visitBiMonthly();
		T visitQuarterly();
		T visitFourMonthly();
		T visitHalfYearly();
	} 
}
