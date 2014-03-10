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
		return mod111.getDetail( Mod111Key.NF_A1 ).getAmount();
	}
	

	@Override
	public Mod111Key getKeyForWorkReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkWitholding() {
		return Mod111Key.NF_A1;
	}

	@Override
	public Mod111Key getKeyForWorkInKindReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkInKindPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForWorkInKindWitholding() {
		return Mod111Key.NF_A1;
	}

	@Override
	public Mod111Key getKeyForInvoiceReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoicePerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceWitholding() {
		return Mod111Key.NF_A1;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindWitholding() {
		return Mod111Key.NF_A1;
	}

	@Override
	public Mod111Key getKeyForFarmerReceivers() {
		return null;
	}

	@Override
	public Mod111Key getKeyForFarmerPerception() {
		return null;
	}

	@Override
	public Mod111Key getKeyForFarmerWitholding() {
		return Mod111Key.NF_A1;

	}
	
}
