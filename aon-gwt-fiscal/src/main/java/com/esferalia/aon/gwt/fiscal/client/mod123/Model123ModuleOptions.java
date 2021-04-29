package com.esferalia.aon.gwt.fiscal.client.mod123;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;

public class Model123ModuleOptions extends  FiscalModelModuleOptions {

	private static final long serialVersionUID = -3009933996710003872L;
	
	private Mod123 newModel;

	public Mod123 getNewModel() {
		return newModel;
	}
	public Model123ModuleOptions setNewModel(Mod123 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}