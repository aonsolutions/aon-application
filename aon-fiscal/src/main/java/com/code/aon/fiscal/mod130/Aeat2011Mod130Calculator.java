package com.code.aon.fiscal.mod130;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2011Mod130Calculator extends FiscalModelDetailCalculator implements IMod130Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod130 mod130) throws AonException {
		calculateDetails(mod130.getDetails());
		double c01 = mod130.getDetail( Mod130Key.C01 ).getAmount();
		double c02 = mod130.getDetail( Mod130Key.C02 ).getAmount();
		double c03 = CommonUtil.round(c01 - c02);
		mod130.getDetail( Mod130Key.C03 ).setAmount( c03 );
		double c04 =  c03>0?CommonUtil.round(c03 * 20 / 100):0;
		mod130.getDetail( Mod130Key.C04 ).setAmount( c04 );
		double c05 = mod130.getDetail( Mod130Key.C05 ).getAmount();
		double c06 = mod130.getDetail( Mod130Key.C06 ).getAmount();
		double c07 =  CommonUtil.round(c04 - c05 - c06);
		
		mod130.getDetail( Mod130Key.C07 ).setAmount( c07 );
		double c08 = mod130.getDetail( Mod130Key.C08 ).getAmount();
		double c09 =  CommonUtil.round(c08 * 2 / 100);
		mod130.getDetail( Mod130Key.C09 ).setAmount( c09 );
		double c10 = mod130.getDetail( Mod130Key.C10 ).getAmount();
		double c11 =  CommonUtil.round(c08 + c09 + c10);
		mod130.getDetail( Mod130Key.C11 ).setAmount( c11 );
		
		double c12 =  CommonUtil.round(c07 + c11);
		mod130.getDetail( Mod130Key.C12 ).setAmount( c12 );
		double c13 = mod130.getDetail( Mod130Key.C13 ).getAmount();
		double c14 =  CommonUtil.round(c12 + c13);
		mod130.getDetail( Mod130Key.C14 ).setAmount( c14 );
		double c15 = mod130.getDetail( Mod130Key.C15 ).getAmount();
		double c16 = mod130.getDetail( Mod130Key.C16 ).getAmount();
		if (c14>0) {
			// TODO
		} else {
			c15 = 0;
			c16 = 0;
		}
		mod130.getDetail( Mod130Key.C15 ).setAmount( c15 );
		mod130.getDetail( Mod130Key.C16 ).setAmount( c16 );
		
		double c17 =  CommonUtil.round(c14 -c15 - c16);
		mod130.getDetail( Mod130Key.C17 ).setAmount( c17 );
		
		double c18 =  mod130.getDetail( Mod130Key.C18 ).getAmount();
		double c19 =  CommonUtil.round(c17 -c18);
		mod130.getDetail( Mod130Key.C19 ).setAmount( c19 );
		
	}

}
