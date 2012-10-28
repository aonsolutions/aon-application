package com.code.aon.ui.fiscal.controller.batch;

import com.code.aon.fiscal.enumeration.FiscalBatchType;

public class FiscalBatchModelFactory {

	
	private static FiscalBatchModelFactory instance; 
	
	private FiscalBatchModelFactory() {
	}
	
	public static FiscalBatchModelFactory getInstance() {
		if (instance == null) {
			instance = new FiscalBatchModelFactory();
		}
		return instance;
	}

	
	public IFiscalBatchModel getFiscalBatchModel(FiscalBatchType mod303) {
		// TODO register Models and return appropiate
		return new Mod303BatchModel();
	}

}
