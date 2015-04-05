package com.code.aon.fiscal.mod131;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod131Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2015Mod131Calculator extends FiscalModelDetailCalculator implements IMod131Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2015 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod131 mod131) throws AonException {
		calculateDetails(mod131.getDetails());
		double c02 = mod131.getDetail( Mod131Key.AC02 ).getAmount();
		
		double c03 = mod131.getDetail( Mod131Key.C03 ).getAmount();
		double c04 =  CommonUtil.round(c03 * 2 / 100);
		mod131.getDetail( Mod131Key.C04 ).setAmount( c04 );
		
		double c05 = mod131.getDetail( Mod131Key.C05 ).getAmount();
		double c06 =  CommonUtil.round(c05 * 2 / 100);
		mod131.getDetail( Mod131Key.C06 ).setAmount( c06 );
		
		double c07 =  CommonUtil.round(c02 + c04 + c06 );
		mod131.getDetail( Mod131Key.C07 ).setAmount( c07 );
		double c08 = mod131.getDetail( Mod131Key.C08 ).getAmount();
		double c09 = mod131.getDetail( Mod131Key.C091 ).getAmount();
		double c10 =  CommonUtil.round(c07 - c08 - c09 );
		mod131.getDetail( Mod131Key.C10 ).setAmount( c10 );		
		
		double c11 =  mod131.getDetail( Mod131Key.C11 ).getAmount();
		double c12 =  mod131.getDetail( Mod131Key.C12 ).getAmount();
		double c13 =  CommonUtil.round(c10 - c11 - c12 );
		mod131.getDetail( Mod131Key.C13 ).setAmount( c13 );
		
		double c14 =  mod131.getDetail( Mod131Key.C14 ).getAmount();
		double c15 =  CommonUtil.round(c13 - c14 );
		mod131.getDetail( Mod131Key.C15 ).setAmount( c15 );
		
	}

	@Override
	public double getResult(Mod131 mod131) {
		return mod131.getDetail( Mod131Key.C15 ).getAmount();
	}

}
