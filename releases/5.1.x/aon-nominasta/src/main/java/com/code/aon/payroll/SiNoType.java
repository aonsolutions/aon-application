package com.code.aon.payroll;

import org.hibernate.type.CharBooleanType;

public class SiNoType extends CharBooleanType {

	@Override
	protected String getFalseString() {
		return "N";
	}

	@Override
	protected String getTrueString() {
		return "S";
	}

}
