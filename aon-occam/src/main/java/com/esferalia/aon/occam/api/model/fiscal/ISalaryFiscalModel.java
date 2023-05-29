package com.esferalia.aon.occam.api.model.fiscal;

public interface ISalaryFiscalModel extends IFiscalModel {

	public boolean mustUseChargeDate();

//	default public boolean mustUseChargeDate() {
//		return false;
//	}
	
}
