package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2018 extends DoubleVariable<Mod2002018Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariable2018() {
		super();
	}
	public DoubleVariable2018(Mod2002018Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2018 clone() {
		DoubleVariable2018 cloned = new DoubleVariable2018( getKey() );
		return cloned;
	}
}
