package com.code.aon.ui.fiscal.controller.mod303;

import com.code.aon.fiscal.enumeration.FiscalModelType;

public class Mod303AIController extends Mod303Controller {
	
	private static final long serialVersionUID = 9044746777170353125L;

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M303_AI;
	}

}
