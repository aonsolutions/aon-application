package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

public class AeatModuleModuleOptions extends  FiscalModelModuleOptions<Mod303> {

	private static final long serialVersionUID = -4243132427760315542L;

	private Mod303 newModel;

	public Mod303 getNewModel() {
		return newModel;
	}
	public AeatModuleModuleOptions setNewModel(Mod303 newModel) {
		this.newModel = newModel;
		return this;
	}
	
	
	
}