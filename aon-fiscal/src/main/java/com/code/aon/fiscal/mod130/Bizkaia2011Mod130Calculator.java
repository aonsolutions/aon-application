package com.code.aon.fiscal.mod130;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Bizkaia2011Mod130Calculator extends FiscalModelDetailCalculator implements IMod130Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.BIZKAIA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod130 mod130) throws AonException {
		calculateDetails(mod130.getDetails());
		double c03 = mod130.getDetail( Mod130Key.C03 ).getAmount();
		double c06 = mod130.getDetail( Mod130Key.C06 ).getAmount();
		FiscalModelDetail detail = mod130.getDetail( Mod130Key.C08 );
		detail.setAmount( CommonUtil.round(c03 + c06) );
	}
}
