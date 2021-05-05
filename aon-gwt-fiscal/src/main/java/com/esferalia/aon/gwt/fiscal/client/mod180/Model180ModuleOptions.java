package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;

public class Model180ModuleOptions extends  FiscalModelModuleOptions {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod180 newModel;

	public Mod180 getNewModel() {
		return newModel;
	}
	public Model180ModuleOptions setNewModel(Mod180 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}