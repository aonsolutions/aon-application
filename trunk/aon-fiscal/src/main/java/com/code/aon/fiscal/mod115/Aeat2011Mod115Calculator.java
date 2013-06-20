package com.code.aon.fiscal.mod115;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod115Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2011Mod115Calculator extends FiscalModelDetailCalculator implements IMod115Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod115 mod115) throws AonException {
		calculateDetails(mod115.getDetails());
		double c03 = mod115.getDetail( Mod115Key.C03 ).getAmount();
		double c07 = mod115.getDetail( Mod115Key.C07 ).getAmount();
		FiscalModelDetail detail = mod115.getDetail( Mod115Key.C08 );
		detail.setAmount( CommonUtil.round(c03 + c07) );
	}

	@Override
	public double getResult(Mod115 mod115) {
		return mod115.getDetail( Mod115Key.C08 ).getAmount();
	}
}
