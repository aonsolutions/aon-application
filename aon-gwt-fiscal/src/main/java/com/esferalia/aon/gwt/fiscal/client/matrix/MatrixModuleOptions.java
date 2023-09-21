package com.esferalia.aon.gwt.fiscal.client.matrix;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class MatrixModuleOptions extends  ModuleOptions<MatrixModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private boolean compactMode;

	public boolean isCompactMode() {
		return compactMode;
	}

	public MatrixModuleOptions setCompactMode(boolean compactMode) {
		this.compactMode = compactMode;
		return this;
	}
	
	

}
