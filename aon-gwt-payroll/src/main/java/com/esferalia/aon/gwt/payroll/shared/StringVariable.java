package com.esferalia.aon.gwt.payroll.shared;

public class StringVariable extends Variable {

	String value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if ( value == null )
			this.value = null;
		else if ( value instanceof String)
			this.value = (String) value;
		else 
			this.value = String.valueOf(value);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}