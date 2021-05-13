package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;

public class Model131ModuleOptions extends  FiscalModelModuleOptions<Mod131> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod131 newModel;

	public Mod131 getNewModel() {
		return newModel;
	}
	public Model131ModuleOptions setNewModel(Mod131 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}