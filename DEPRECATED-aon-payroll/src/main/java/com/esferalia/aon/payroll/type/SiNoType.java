package com.esferalia.aon.payroll.type;

import org.hibernate.type.CharBooleanType;

public class SiNoType extends CharBooleanType {

	private static final long serialVersionUID = 7530418105824710733L;

	@Override
	protected String getFalseString() {
		return "N";
	}

	@Override
	protected String getTrueString() {
		return "S";
	}

}
