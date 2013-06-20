package com.code.aon.fiscal.mod115;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Navarra2011Mod115Calculator extends FiscalModelDetailCalculator implements IMod115Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.NAVARRA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod115 mod115) throws AonException {
		calculateDetails(mod115.getDetails());
	}

	@Override
	public double getResult(Mod115 mod115) {
		return 0;
	}
}
