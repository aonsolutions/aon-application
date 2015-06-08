package com.esferalia.aon.occam.api.model.fiscal.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Variable;


public class DoubleVariable<K extends IMod200Key> extends Variable<K,Double> {

	private static final long serialVersionUID = 1L;
	
	private Double value;

	public DoubleVariable() {
		super();
	}
	public DoubleVariable(K key) {
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
	public DoubleVariable<K> clone() {
		DoubleVariable<K> cloned = new DoubleVariable<K>( getKey() );
		return cloned;
	}

}
