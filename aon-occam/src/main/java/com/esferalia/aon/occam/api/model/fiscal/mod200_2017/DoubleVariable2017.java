package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2017 extends DoubleVariable<Mod2002017Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariable2017() {
		super();
	}
	public DoubleVariable2017(Mod2002017Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2017 clone() {
		DoubleVariable2017 cloned = new DoubleVariable2017( getKey() );
		return cloned;
	}
}
