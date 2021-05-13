package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;

public class Model190ModuleOptions extends  FiscalModelModuleOptions<Mod190> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod190 newModel;

	public Mod190 getNewModel() {
		return newModel;
	}
	public Model190ModuleOptions setNewModel(Mod190 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}