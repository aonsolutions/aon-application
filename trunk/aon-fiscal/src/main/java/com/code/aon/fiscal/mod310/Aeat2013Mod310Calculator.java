package com.code.aon.fiscal.mod310;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod310Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2013Mod310Calculator extends FiscalModelDetailCalculator implements IMod310Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod310 mod310) throws AonException {
		calculateDetails(mod310.getDetails());
		double c01 = 0.0;
		for (FiscalModelDetail detail : mod310.getDetails()) {
			if ( StringUtils.startsWith( detail.getType() , Mod310Key.ACTIVITIES_PREFIX ) 
			  || StringUtils.startsWith( detail.getType() , Mod310Key.FARMING_ACTIVITIES_PREFIX)) {
				c01 = c01 + detail.getAmount();
			}
		}
		mod310.getDetail( Mod310Key.C01 ).setAmount( CommonUtil.round(c01) );
		
		double c02 = mod310.getDetail( Mod310Key.C02 ).getAmount();
		double c03 = mod310.getDetail( Mod310Key.C03 ).getAmount();
		double c04 = mod310.getDetail( Mod310Key.C04 ).getAmount();
		double c05 = CommonUtil.round(c01 + c02 + c03  + c04);
		mod310.getDetail( Mod310Key.C05 ).setAmount( c05 );
		
		double c06 = mod310.getDetail( Mod310Key.C06 ).getAmount();
		
		double c07 = CommonUtil.round(c05 - c06);
		mod310.getDetail( Mod310Key.C07 ).setAmount( c07 );
		
		double c08 = mod310.getDetail( Mod310Key.C08 ).getAmount();
		double c09 = CommonUtil.round(c07 - c08);
		mod310.getDetail( Mod310Key.C09 ).setAmount( c09 );
		double c11 = mod310.getDetail( Mod310Key.C11 ).getAmount();
		double c12 = CommonUtil.round(c09 - c11);
		mod310.getDetail( Mod310Key.C12 ).setAmount( c12 );
	}

}
