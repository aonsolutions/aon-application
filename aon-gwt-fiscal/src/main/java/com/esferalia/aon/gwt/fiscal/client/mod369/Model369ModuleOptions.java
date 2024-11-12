package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;

public class Model369ModuleOptions extends  FiscalModelModuleOptions<Mod369> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod369 newModel;

	public Mod369 getNewModel() {
		return newModel;
	}
	public Model369ModuleOptions setNewModel(Mod369 newModel) {
		this.newModel = newModel;
		return this;
	}
	
}