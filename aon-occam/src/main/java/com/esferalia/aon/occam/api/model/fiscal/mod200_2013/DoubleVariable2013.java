package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2013 extends DoubleVariable<Mod2002013Key> {

	private static final long serialVersionUID = 5835691472921754211L;

	public DoubleVariable2013() {
		super();
	}
	public DoubleVariable2013(Mod2002013Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2013 clone() {
		DoubleVariable2013 cloned = new DoubleVariable2013( getKey() );
		return cloned;
	}
}
