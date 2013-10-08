package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Alava2011Mod111Calculator extends FiscalModelDetailCalculator implements IMod111Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.ALAVA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod111 mod111) throws AonException {
		calculateDetails(mod111.getDetails());
		double c03 = mod111.getDetail( Mod111Key.AR_C03 ).getAmount();
		double c06 = mod111.getDetail( Mod111Key.AR_C06 ).getAmount();
		double c09 = mod111.getDetail( Mod111Key.AR_C09 ).getAmount();
		double c12 = mod111.getDetail( Mod111Key.AR_C12 ).getAmount();
		double c15 = mod111.getDetail( Mod111Key.AR_C15 ).getAmount();
		double c18 = mod111.getDetail( Mod111Key.AR_C18 ).getAmount();
		double c21 = mod111.getDetail( Mod111Key.AR_C21 ).getAmount();
		double c24 = mod111.getDetail( Mod111Key.AR_C24 ).getAmount();
		double c27 = mod111.getDetail( Mod111Key.AR_C27 ).getAmount();
		FiscalModelDetail detail = mod111.getDetail( Mod111Key.AR_C28 );
		detail.setAmount( CommonUtil.round(c03+c06+c09+c12+c15+c18+c21+c24+c27) );
		double c28 = detail.getAmount();
		double c29 = mod111.getDetail( Mod111Key.AR_C29 ).getAmount();
		double c30 = mod111.getDetail( Mod111Key.AR_C30 ).getAmount();
		detail = mod111.getDetail( Mod111Key.AR_C31 );
		detail.setAmount( CommonUtil.round(c28+c29+c30) );
	}

	@Override
	public Mod111Key getKeyForWorkReceivers() {
		return Mod111Key.AR_C01;
	}

	@Override
	public Mod111Key getKeyForWorkPerception() {
		return Mod111Key.AR_C02;
	}

	@Override
	public Mod111Key getKeyForWorkWitholding() {
		return Mod111Key.AR_C03;
	}

	@Override
	public Mod111Key getKeyForWorkInKindReceivers() {
		return Mod111Key.AR_C25;
	}

	@Override
	public Mod111Key getKeyForWorkInKindPerception() {
		return Mod111Key.AR_C26;
	}

	@Override
	public Mod111Key getKeyForWorkInKindWitholding() {
		return Mod111Key.AR_C27;
	}
	
	@Override
	public Mod111Key getKeyForInvoiceReceivers() {
		return Mod111Key.AR_C13;
	}

	@Override
	public Mod111Key getKeyForInvoicePerception() {
		return Mod111Key.AR_C14;
	}

	@Override
	public Mod111Key getKeyForInvoiceWitholding() {
		return Mod111Key.AR_C15;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindReceivers() {
		return Mod111Key.AR_C25;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindPerception() {
		return Mod111Key.AR_C26;
	}

	@Override
	public Mod111Key getKeyForInvoiceInKindWitholding() {
		return Mod111Key.AR_C27;
	}
	
	@Override
	public double getResult(Mod111 mod111) {
		return mod111.getDetail( Mod111Key.AR_C31 ).getAmount();
	}
	
}
