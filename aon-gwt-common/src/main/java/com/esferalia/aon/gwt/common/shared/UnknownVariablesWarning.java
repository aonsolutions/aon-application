package com.esferalia.aon.gwt.common.shared;



public class UnknownVariablesWarning extends EvalWarning {
	
	
	
	private String names [];
	
	// For serialize
	public UnknownVariablesWarning() {
		super();
		names = new String[]{};
	}

	public UnknownVariablesWarning(String ...names) {
		super();
		this.names = names;
	}
	
	public String[] getNames() {
		return names;
	}

}
