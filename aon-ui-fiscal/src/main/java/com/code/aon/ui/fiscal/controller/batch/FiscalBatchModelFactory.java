package com.code.aon.ui.fiscal.controller.batch;

import com.code.aon.common.AonException;
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

	
	public IFiscalBatchModel getFiscalBatchModel(FiscalBatchType type) throws AonException {
		if (type == FiscalBatchType.MOD303) {
			return new Mod303BatchModel();	
		} else if (type == FiscalBatchType.MOD111) {
			return new Mod111BatchModel();
		} else if (type == FiscalBatchType.MOD115) {
			return new Mod115BatchModel();
		} else if (type == FiscalBatchType.MOD123) {
			return new Mod123BatchModel();
		} else if (type == FiscalBatchType.MOD130) {
			return new Mod130BatchModel();
		} else if (type == FiscalBatchType.MOD131) {
			return new Mod131BatchModel();
		}
		throw new AonException("Imposible recuperar las declaraciones del tipo " + type);
	}

}
