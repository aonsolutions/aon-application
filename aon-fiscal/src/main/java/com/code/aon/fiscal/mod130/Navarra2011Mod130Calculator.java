package com.code.aon.fiscal.mod130;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Navarra2011Mod130Calculator extends FiscalModelDetailCalculator implements IMod130Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.NAVARRA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod130 mod130) throws AonException {
		calculateDetails(mod130.getDetails());
	}
}
