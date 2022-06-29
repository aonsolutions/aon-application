package com.esferalia.aon.occam.mod200.api.model;

public class DoubleVariableEx extends DoubleVariable<IMod200Key> {
	
	private static final long serialVersionUID = -7160165062817151718L;

	public DoubleVariableEx() {
		super();
	}
	public DoubleVariableEx(IMod200Key key) {
		super(key);
	}
	
	@Override
	public DoubleVariableEx clone() {
		DoubleVariableEx cloned = new DoubleVariableEx( getKey() );
		return cloned;
	}
}
