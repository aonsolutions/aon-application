package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;

public class Model130ModuleOptions extends  FiscalModelModuleOptions<Mod130> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod130 newModel;

	public Mod130 getNewModel() {
		return newModel;
	}
	public Model130ModuleOptions setNewModel(Mod130 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}