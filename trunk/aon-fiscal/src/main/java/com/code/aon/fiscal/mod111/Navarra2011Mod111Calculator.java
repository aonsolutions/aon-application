package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Navarra2011Mod111Calculator extends FiscalModelDetailCalculator implements IMod111Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.NAVARRA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod111 mod111) throws AonException {
		calculateDetails(mod111.getDetails());
	}

	@Override
	public Mod111Key getKeyForReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForWitholding() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInKindReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInKindPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInKindWitholding() {
		return null;
	}
}
