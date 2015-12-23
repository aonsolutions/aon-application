package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum BillingPeriod implements Serializable{
	NO_PERIOD(0),
	MONTHLY(1),
	BI_MONTHLY(2),
	THREE_MONTHLY(3),
	FOUR_MONTHLY(4),
	SIX_MONTHLY(6),
	YEARLY(12);

   
   private int value;
   
   BillingPeriod(int value){
   	this.value = value;
   }

   public String getName() {
	   return this.toString();
   }
   
   public int getValue(){
	   return this.value;
   }
   
   public byte value(){
	   return (byte) this.ordinal();
   }
}
