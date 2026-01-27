package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

public enum BillingPeriod implements Serializable{
	NO_PERIOD		(""),
	MONTHLY			("Mensual"),
	BI_MONTHLY		("Bimensual"),
	THREE_MONTHLY	("Trimestal"),
	FOUR_MONTHLY	("Cuatrimestral"),
	SIX_MONTHLY		("Semestral"),
	YEARLY			("Anual")
	;	

   
   private String description;
   
   private BillingPeriod(String description){
	   this.description = description;
   }
   
   public String getDescription() {
	   return this.description;
   }

   public String getName() {
	   return this.toString();
   }
   
   public Integer getValue(){
	   return this.ordinal();
   }
   
   public short value(){
	   return (short) this.ordinal();
   }
   
	public static String toString(BillingPeriod period) {
		if (period == null) return "";
		return period.getName();
	}

	public static Optional<BillingPeriod> safeValueOf(Short s) {
		if (s == null) return Optional.empty();
		return safeValueOf(s.intValue());
	}

	public static Optional<BillingPeriod> safeValueOf(Integer i) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= BillingPeriod.values().length) return Optional.empty();
		return Optional.of(BillingPeriod.values()[i]);
	}
	
	/**
	 * @deprecated NULL is null NO_PERIOD is NO_PERIOD!!
	 */
	@Deprecated
	public static BillingPeriod safeValueOf( String value ) {
		if(value == null) return BillingPeriod.NO_PERIOD;
		for(BillingPeriod p : BillingPeriod.values()) {
			if(p.name().equalsIgnoreCase(value) || Integer.toString(p.getValue()).equals(value) || Double.toString(p.getValue().doubleValue()).equals(value)) {
				return p;
			}
		}
		return safeValueOf2(value);
	}
	
	/**
	 * @deprecated NULL is null NO_PERIOD is NO_PERIOD!!
	 */
	@Deprecated
	public static BillingPeriod safeValueOf2(String value) {
		if("mensual".equalsIgnoreCase(value)) {
			return BillingPeriod.MONTHLY;
		} else if("bimensual".equalsIgnoreCase(value)) {
			return BillingPeriod.BI_MONTHLY;
		} else if("trimestral".equalsIgnoreCase(value)) {
			return BillingPeriod.THREE_MONTHLY;
		} else if("cuatrimestral".equalsIgnoreCase(value)) {
			return BillingPeriod.FOUR_MONTHLY;
		} else if("semestral".equalsIgnoreCase(value)) {
			return BillingPeriod.SIX_MONTHLY;
		} else if("anual".equalsIgnoreCase(value)) {
			return BillingPeriod.YEARLY;
		}
		return BillingPeriod.NO_PERIOD;
	}
	
	
}
