package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import com.esferalia.aon.occam.mod200.api.model.DoubleVariable;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

public class DoubleVariable2020 extends DoubleVariable<IMod200Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariable2020() {
		super();
	}
	public DoubleVariable2020(IMod200Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2020 clone() {
		DoubleVariable2020 cloned = new DoubleVariable2020( getKey() );
		return cloned;
	}
}
