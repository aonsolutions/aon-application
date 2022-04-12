package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;


public class DoubleVariable2015 extends DoubleVariable<Mod2002015Key> {
	
	private static final long serialVersionUID = -5547645316652424380L;
	//private static final long serialVersionUID = -5065308788273775584L;
	
	public DoubleVariable2015() {
		super();
	}
	public DoubleVariable2015(Mod2002015Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2015 clone() {
		DoubleVariable2015 cloned = new DoubleVariable2015( getKey() );
		return cloned;
	}
}
