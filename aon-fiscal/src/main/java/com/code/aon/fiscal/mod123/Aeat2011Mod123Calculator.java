package com.code.aon.fiscal.mod123;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod123Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2011Mod123Calculator extends FiscalModelDetailCalculator implements IMod123Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod123 mod123) throws AonException {
		calculateDetails(mod123.getDetails());
		double c03 = mod123.getDetail( Mod123Key.C03 ).getAmount();
		double c08 = mod123.getDetail( Mod123Key.C08 ).getAmount();
		FiscalModelDetail detail = mod123.getDetail( Mod123Key.C09 );
		detail.setAmount( CommonUtil.round(c03 + c08) );
		double c09 = detail.getAmount();
		double c10 = mod123.getDetail( Mod123Key.C10 ).getAmount();
		mod123.getDetail( Mod123Key.C13 ).setAmount( CommonUtil.round(c09 + c10) );
	}

}
