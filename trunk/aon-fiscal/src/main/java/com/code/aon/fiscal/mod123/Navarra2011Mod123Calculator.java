package com.code.aon.fiscal.mod123;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Navarra2011Mod123Calculator extends FiscalModelDetailCalculator implements IMod123Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.NAVARRA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod123 mod123) throws AonException {
		calculateDetails(mod123.getDetails());
	}

	@Override
	public double getResult(Mod123 mod123) {
		return 0;
	}
}
