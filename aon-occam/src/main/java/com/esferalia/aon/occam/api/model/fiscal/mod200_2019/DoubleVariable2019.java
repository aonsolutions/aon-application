package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import com.esferalia.aon.occam.api.model.fiscal.mod200.DoubleVariable;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;

public class DoubleVariable2019 extends DoubleVariable<IMod200Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariable2019() {
		super();
	}
	public DoubleVariable2019(IMod200Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariable2019 clone() {
		DoubleVariable2019 cloned = new DoubleVariable2019( getKey() );
		return cloned;
	}
}
