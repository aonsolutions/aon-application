package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2014 extends DoubleVariable<Mod2002014Key> {

	private static final long serialVersionUID = -5065308788273775584L;
	
	public DoubleVariable2014() {
		super();
	}
	public DoubleVariable2014(Mod2002014Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2014 clone() {
		DoubleVariable2014 cloned = new DoubleVariable2014( getKey() );
		return cloned;
	}
}
