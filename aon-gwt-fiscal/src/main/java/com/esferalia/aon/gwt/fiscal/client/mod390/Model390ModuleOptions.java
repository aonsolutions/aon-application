package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;

public class Model390ModuleOptions extends  FiscalModelModuleOptions<Mod390> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod390 newModel;

	public Mod390 getNewModel() {
		return newModel;
	}
	public Model390ModuleOptions setNewModel(Mod390 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}