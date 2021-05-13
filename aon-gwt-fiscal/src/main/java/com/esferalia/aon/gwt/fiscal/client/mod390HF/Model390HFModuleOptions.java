package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;

public class Model390HFModuleOptions extends  FiscalModelModuleOptions<Mod390HF> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod390HF newModel;

	public Mod390HF getNewModel() {
		return newModel;
	}
	public Model390HFModuleOptions setNewModel(Mod390HF newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}