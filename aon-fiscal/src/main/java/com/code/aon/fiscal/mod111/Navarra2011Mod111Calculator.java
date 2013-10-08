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
	public double getResult(Mod111 mod111) {
		return 0;
	}

	@Override
	public Mod111Key getKeyForWorkReceivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkPerception() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkWitholding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkInKindReceivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkInKindPerception() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkInKindWitholding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceReceivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoicePerception() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceWitholding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindReceivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindPerception() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindWitholding() {
		// TODO Auto-generated method stub
		return null;
	}
}
