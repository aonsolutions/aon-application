package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;

public class Model202ModuleOptions extends  FiscalModelModuleOptions {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod202 newModel;

	public Mod202 getNewModel() {
		return newModel;
	}
	public Model202ModuleOptions setNewModel(Mod202 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}