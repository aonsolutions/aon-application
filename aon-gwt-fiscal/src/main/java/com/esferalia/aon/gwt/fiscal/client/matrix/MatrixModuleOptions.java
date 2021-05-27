package com.esferalia.aon.gwt.fiscal.client.matrix;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class MatrixModuleOptions extends  ModuleOptions<MatrixModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private AonData aonData;

	public AonData getAonData() {
		return aonData;
	}
	public MatrixModuleOptions setAonData(AonData aonData) {
		this.aonData = aonData;
		return this;
	}


}
