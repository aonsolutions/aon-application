package com.esferalia.aon.gwt.payroll.shared;

public class NumberVariable extends Variable {

	Number value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value == null)
			this.value = null;
		else if (value instanceof Number)
			this.value = (Number) value;
		else if (value instanceof String)
			this.value = Double.valueOf((String) value);
		else
			throw new IllegalArgumentException(value
					+ " can't be assigned to a '" + Number.class.getName()
					+ "'");
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public double doubleValue() {
		return value != null ? value.doubleValue() : 0.00;
	}

	
	

}