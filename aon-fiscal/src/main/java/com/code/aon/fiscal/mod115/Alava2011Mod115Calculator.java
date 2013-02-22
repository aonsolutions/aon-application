package com.code.aon.fiscal.mod115;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod115Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Alava2011Mod115Calculator extends FiscalModelDetailCalculator implements IMod115Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.ALAVA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod115 mod115) throws AonException {
		calculateDetails(mod115.getDetails());
		double c03 = mod115.getDetail( Mod115Key.C03 ).getAmount();
		double c06 = mod115.getDetail( Mod115Key.C06 ).getAmount();
		FiscalModelDetail detail = mod115.getDetail( Mod115Key.C08 );
		double c08 = CommonUtil.round(c03 + c06);
		detail.setAmount( c08 );
		double c09 = mod115.getDetail( Mod115Key.C09 ).getAmount();
		double c10 = mod115.getDetail( Mod115Key.C10 ).getAmount();
		detail = mod115.getDetail( Mod115Key.C11 );
		detail.setAmount( CommonUtil.round(c08 + c09 + c10) );
	}
	
	@Override
	public double getResult(Mod115 mod115) {
		return mod115.getDetail( Mod115Key.C11 ).getAmount();
	}
	
}
