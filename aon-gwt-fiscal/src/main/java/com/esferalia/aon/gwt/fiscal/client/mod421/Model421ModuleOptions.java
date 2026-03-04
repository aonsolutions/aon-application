package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;

public class Model421ModuleOptions extends  FiscalModelModuleOptions<Mod421> {

	private static final long serialVersionUID = -4866057685353737943L;
	
	private Mod421 newModel;

	public Mod421 getNewModel() {
		return newModel;
	}
	public Model421ModuleOptions setNewModel(Mod421 newModel) {
		this.newModel = newModel;
		return this;
	}
	
}