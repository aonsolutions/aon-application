package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2016 extends DoubleVariable<Mod2002016Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariable2016() {
		super();
	}
	public DoubleVariable2016(Mod2002016Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2016 clone() {
		DoubleVariable2016 cloned = new DoubleVariable2016( getKey() );
		return cloned;
	}
}
