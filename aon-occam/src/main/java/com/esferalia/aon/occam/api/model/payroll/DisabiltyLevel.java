package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;

public enum DisabiltyLevel implements Serializable{
	GT_EQ_33_LT_65,
	GT_EQ_33_LT_65_DEPENDENCE,
	GT_EQ_65
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
}
