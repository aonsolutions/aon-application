package com.esferalia.aon.gwt.mod200.client.mod200;

import com.esferalia.aon.gwt.mod200.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.mod200.api.model.Mod200;

public class Model200ModuleOptions extends FiscalModelModuleOptions<Mod200> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod200 newModel;

	public Mod200 getNewModel() {
		return newModel;
	}
	public Model200ModuleOptions setNewModel(Mod200 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}