package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum BillingPeriod implements Serializable{
	NO_PERIOD(0),
	MONTHLY(1),
	BI_MONTHLY(2),
	THREE_MONTHLY(3),
	FOUR_MONTHLY(4),
	SIX_MONTHLY(5),
	YEARLY(6);

   
   private int value;
   
   BillingPeriod(int value){
   	this.value = value;
   }

   public String getName() {
	   return this.toString();
   }
   
   public Integer getValue(){
	   return this.value;
   }
   
   public byte value(){
	   return (byte) this.ordinal();
   }
   
	public static BillingPeriod safeValueOf( String value ) {
		if(value == null) return BillingPeriod.NO_PERIOD;
		for(BillingPeriod p : BillingPeriod.values()) {
			if(p.name().equalsIgnoreCase(value) || Integer.toString(p.getValue()).equals(value) || Double.toString(p.getValue().doubleValue()).equals(value)) {
				return p;
			}
		}
		return safeValueOf2(value);
	}
	
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
