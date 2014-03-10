package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Gipuzkoa2011Mod111Calculator extends FiscalModelDetailCalculator implements IMod111Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.GIPUZKOA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod111 mod111) throws AonException {
		calculateDetails(mod111.getDetails());
		double c03 = mod111.getDetail( Mod111Key.GP_C03 ).getAmount();
		double c06 = mod111.getDetail( Mod111Key.GP_C06 ).getAmount();
		double c09 = mod111.getDetail( Mod111Key.GP_C09 ).getAmount();
		double c12 = mod111.getDetail( Mod111Key.GP_C12 ).getAmount();
		double c15 = mod111.getDetail( Mod111Key.GP_C15 ).getAmount();
		double c18 = mod111.getDetail( Mod111Key.GP_C18 ).getAmount();
		double c21 = mod111.getDetail( Mod111Key.GP_C21 ).getAmount();
		double c24 = mod111.getDetail( Mod111Key.GP_C24 ).getAmount();
		FiscalModelDetail detail = mod111.getDetail( Mod111Key.GP_C25 );
		detail.setAmount( CommonUtil.round(c03+c06+c09+c12+c15+c18+c21+c24) );
	}

	@Override
	public Mod111Key getKeyForWorkReceivers() {
		return Mod111Key.GP_C01;
	}

	@Override
	public Mod111Key getKeyForWorkPerception() {
		return Mod111Key.GP_C02;
	}

	@Override
	public Mod111Key getKeyForWorkWitholding() {
		return Mod111Key.GP_C03;
	}

	@Override
	public Mod111Key getKeyForWorkInKindReceivers() {
		return Mod111Key.GP_C01;
	}

	@Override
	public Mod111Key getKeyForWorkInKindPerception() {
		return Mod111Key.GP_C02;
	}

	@Override
	public Mod111Key getKeyForWorkInKindWitholding() {
		return Mod111Key.GP_C03;
	}

	@Override
	public Mod111Key getKeyForInvoiceReceivers() {
		return Mod111Key.GP_C04;
	}

	@Override
	public Mod111Key getKeyForInvoicePerception() {
		return Mod111Key.GP_C05;
	}

	@Override
	public Mod111Key getKeyForInvoiceWitholding() {
		return Mod111Key.GP_C06;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindReceivers() {
		return Mod111Key.GP_C04;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindPerception() {
		return Mod111Key.GP_C05;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindWitholding() {
		return Mod111Key.GP_C06;
	}

	@Override
	public Mod111Key getKeyForFarmerReceivers() {
		return Mod111Key.GP_C07;
	}

	@Override
	public Mod111Key getKeyForFarmerPerception() {
		return Mod111Key.GP_C08;
	}

	@Override
	public Mod111Key getKeyForFarmerWitholding() {
		return Mod111Key.GP_C09;
	}

	@Override
	public double getResult(Mod111 mod111) {
		return mod111.getDetail( Mod111Key.GP_C25 ).getAmount();
	}
	
}
