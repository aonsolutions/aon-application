package com.esferalia.aon.occam.api.model.fiscal.mod200;


public class DoubleVariable extends Variable<Double> {

	private static final long serialVersionUID = 1L;
	
	private Double value;

	public DoubleVariable() {
		super();
	}
	public DoubleVariable(Mod200Key key) {
		super(key);
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void setValue(Double value) {
		this.value = value;
	}

	public void setValue(Boolean value) {
		this.value = value?1.0:0.0;
	}

	@Override
	public DoubleVariable clone() {
		DoubleVariable cloned = new DoubleVariable( getKey() );
		return cloned;
	}

}
