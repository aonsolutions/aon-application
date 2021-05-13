package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;

public class Model111ModuleOptions extends  FiscalModelModuleOptions<Mod111> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod111 newModel;

	public Mod111 getNewModel() {
		return newModel;
	}
	public Model111ModuleOptions setNewModel(Mod111 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}