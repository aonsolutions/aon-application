package com.code.aon.fiscal.mod311;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod311Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2013Mod311Calculator extends FiscalModelDetailCalculator implements IMod311Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod311 mod311) throws AonException {
		calculateDetails(mod311.getDetails());
		double c01 = 0.0;
		for (FiscalModelDetail detail : mod311.getDetails()) {
			if ( StringUtils.startsWith( detail.getType() , Mod311Key.ACTIVITIES_PREFIX ) 
			  || StringUtils.startsWith( detail.getType() , Mod311Key.FARMING_ACTIVITIES_PREFIX)) {
				c01 = c01 + detail.getAmount();
			}
		}
		mod311.getDetail( Mod311Key.C01 ).setAmount( CommonUtil.round(c01) );
		double c02 = mod311.getDetail( Mod311Key.C02 ).getAmount();
		double c03 = CommonUtil.round(c01 - c02);
		mod311.getDetail( Mod311Key.C03 ).setAmount( CommonUtil.round(c03) );

		double c04 = mod311.getDetail( Mod311Key.C04 ).getAmount();
		double c05 = mod311.getDetail( Mod311Key.C05 ).getAmount();
		double c06 = mod311.getDetail( Mod311Key.C06 ).getAmount();
		double c07 = CommonUtil.round(c03 + c04 + c05  + c06);
		mod311.getDetail( Mod311Key.C07 ).setAmount( c07 );
		
		double c08 = mod311.getDetail( Mod311Key.C08 ).getAmount();
		double c09 = mod311.getDetail( Mod311Key.C09 ).getAmount();
		double c10 = CommonUtil.round(c08 + c09);
		mod311.getDetail( Mod311Key.C10 ).setAmount( c10 );
		
		double c11 = CommonUtil.round(c07 - c10);
		mod311.getDetail( Mod311Key.C11 ).setAmount( c11 );
		
		double c12 = mod311.getDetail( Mod311Key.C12 ).getAmount();
		double c13 = CommonUtil.round(c11 - c12);
		mod311.getDetail( Mod311Key.C13 ).setAmount( c13 );
		
		double c15 = mod311.getDetail( Mod311Key.C15 ).getAmount();
		double c16 = CommonUtil.round(c13 - c15);
		mod311.getDetail( Mod311Key.C16 ).setAmount( c16 );
	}

	@Override
	public double getResult(Mod311 mod311) {
		return mod311.getDetail( Mod311Key.C16 ).getAmount();
	}

}
