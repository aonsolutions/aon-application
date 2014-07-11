package com.code.aon.fiscal.mod303;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2014Mod303Calculator extends FiscalModelDetailCalculator implements IMod303Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod303 mod303) throws AonException {
		calculateDetails(mod303.getDetails());
		
		if (mod303.getDetail(Mod303Key.CAC1) != null) {
			double z1 = mod303.ensureAmount( Mod303Key.CAC1_Z);
			double zd1 = mod303.ensureAmount( Mod303Key.CAC1_ZD);
			double za1 = 0;
			if (z1 == 0) {
				z1 = 1;
				za1 = 90;
				if (zd1 == 0) {
					zd1 = 90;
				}
			} else {
				za1 = mod303.ensureAmount( Mod303Key.CAC1_ZA);
			}
			double c1 = mod303.ensureAmount( Mod303Key.CAC1_C);
			double d1 = mod303.ensureAmount( Mod303Key.CAC1_D);
			double e1 = mod303.ensureAmount( Mod303Key.CAC1_E);
			double f1 = CommonUtil.round( (c1 - d1) * e1 / 100 );
			f1 = CommonUtil.round(f1 * z1 );
			f1 = CommonUtil.round( f1 * zd1 / za1 );
			mod303.getDetail( Mod303Key.CAC1_F ).setAmount( f1 );
		}
		
		if (mod303.getDetail(Mod303Key.CAC2) != null) {
			double z2 = mod303.ensureAmount( Mod303Key.CAC2_Z);
			double za2 = mod303.ensureAmount( Mod303Key.CAC2_ZA);
			double zd2 = mod303.ensureAmount( Mod303Key.CAC2_ZD);
			if (z2 == 0) {
				z2 = 1;
				za2 = 90;
				if (zd2 == 0) {
					zd2 = 90;
				}
			} else {
				za2 = mod303.ensureAmount( Mod303Key.CAC2_ZA);
			}
			double c2 = mod303.ensureAmount( Mod303Key.CAC2_C);
			double d2 = mod303.ensureAmount( Mod303Key.CAC2_D);
			double e2 = mod303.ensureAmount( Mod303Key.CAC2_E);
			double f2 = CommonUtil.round( (c2 - d2) * e2 / 100 );
			f2 = CommonUtil.round(f2 * z2 );
			f2 = CommonUtil.round( f2 * zd2 / za2 );
			mod303.getDetail( Mod303Key.CAC2_F ).setAmount( f2 );
		}
		
		if (mod303.getDetail(Mod303Key.CAC3) != null) {
			double z3 = mod303.ensureAmount( Mod303Key.CAC3_Z);
			double za3 = mod303.ensureAmount( Mod303Key.CAC3_ZA);
			double zd3 = mod303.ensureAmount( Mod303Key.CAC3_ZD);
			if (z3 == 0) {
				z3 = 1;
				za3 = 90;
				if (zd3 == 0) {
					zd3 = 90;
				}
			} else {
				za3 = mod303.ensureAmount( Mod303Key.CAC3_ZA);
			}
			double c3 = mod303.ensureAmount( Mod303Key.CAC3_C);
			double d3 = mod303.ensureAmount( Mod303Key.CAC3_D);
			double e3 = mod303.ensureAmount( Mod303Key.CAC3_E);
			double f3 = CommonUtil.round( (c3 - d3) * e3 / 100 );
			f3 = CommonUtil.round(f3 * z3 );
			f3 = CommonUtil.round( f3 * zd3 / za3 );
			mod303.getDetail( Mod303Key.CAC3_F ).setAmount( f3 );
		}
		
		if (mod303.getDetail(Mod303Key.CAC4) != null) {
			double z4 = mod303.ensureAmount( Mod303Key.CAC4_Z);
			double za4 = mod303.ensureAmount( Mod303Key.CAC4_ZA);
			double zd4 = mod303.ensureAmount( Mod303Key.CAC4_ZD);
			if (z4 == 0) {
				z4 = 1;
				za4 = 90;
				if (zd4 == 0) {
					zd4 = 90;
				}
			} else {
				za4 = mod303.ensureAmount( Mod303Key.CAC4_ZA);
			}
			double c4 = mod303.ensureAmount( Mod303Key.CAC4_C);
			double d4 = mod303.ensureAmount( Mod303Key.CAC4_D);
			double e4 = mod303.ensureAmount( Mod303Key.CAC4_E);
			double f4 = CommonUtil.round( (c4 - d4) * e4 / 100 );
			f4 = CommonUtil.round(f4 * z4 );
			f4 = CommonUtil.round( f4 * zd4 / za4 );
			mod303.getDetail( Mod303Key.CAC4_F ).setAmount( f4 );
		}

		double c47 = 0.0;
		double c48 = 0.0;
		
		if (mod303.isLastPeriod()) {
			double ca1 = mod303.ensureAmount( Mod303Key.CAG1_V7);
			double ca2 = mod303.ensureAmount( Mod303Key.CAG2_V7 );
			double ca3 = mod303.ensureAmount( Mod303Key.CAG3_V7 );
			double ca4 = mod303.ensureAmount( Mod303Key.CAG4_V7 );

			double c1 = mod303.ensureAmount( Mod303Key.CAC1_M );
			double c2 = mod303.ensureAmount( Mod303Key.CAC2_M );
			double c3 = mod303.ensureAmount( Mod303Key.CAC3_M );
			double c4 = mod303.ensureAmount( Mod303Key.CAC4_M );
			c48 = CommonUtil.round( ca1 + ca2 + ca3 + ca4 + c1 + c2 + c3 + c4 );	
		} else {
			double ca1 = mod303.ensureAmount( Mod303Key.CAG1_V5);
			double ca2 = mod303.ensureAmount( Mod303Key.CAG2_V5 );
			double ca3 = mod303.ensureAmount( Mod303Key.CAG3_V5 );
			double ca4 = mod303.ensureAmount( Mod303Key.CAG4_V5 );

			double c1 = mod303.ensureAmount( Mod303Key.CAC1_F );
			double c2 = mod303.ensureAmount( Mod303Key.CAC2_F );
			double c3 = mod303.ensureAmount( Mod303Key.CAC3_F );
			double c4 = mod303.ensureAmount( Mod303Key.CAC4_F );
			c47 = CommonUtil.round( ca1 + ca2 + ca3 + ca4 + c1 + c2 + c3 + c4 );	
		}			
		
		double c51 = mod303.getDetail( Mod303Key.C51 ).getAmount();
		double c52 = mod303.getDetail( Mod303Key.C52 ).getAmount();
		double c53 = mod303.getDetail( Mod303Key.C53 ).getAmount();
		double c54 = 0.0; 
		if (mod303.isLastPeriod()) {
			mod303.getDetail( Mod303Key.C48 ).setAmount( CommonUtil.round(c47) );
			double c49 = mod303.getDetail( Mod303Key.C49 ).getAmount();
			double c50 = CommonUtil.round(c48 - c49);
			mod303.getDetail( Mod303Key.C50 ).setAmount( c50 );
			c54 = CommonUtil.round(c50 + c51 + c52 + c53);
		} else {
			mod303.getDetail( Mod303Key.C47 ).setAmount( CommonUtil.round(c47) );
			c54 = CommonUtil.round(c47 + c51 + c52 + c53);
		}
		mod303.getDetail( Mod303Key.C54 ).setAmount( c54 );
		double c55 = mod303.getDetail( Mod303Key.C55 ).getAmount();
		double c56 = mod303.getDetail( Mod303Key.C56 ).getAmount();
		double c57 = CommonUtil.round(c55 + c56);
		mod303.getDetail( Mod303Key.C57 ).setAmount( c57 );
		
		double c58 = CommonUtil.round(c54 - c57);
		mod303.getDetail( Mod303Key.C58 ).setAmount( c58 );
		double c64 = c58;
		mod303.getDetail( Mod303Key.C64 ).setAmount( c64 );
		double c65 = mod303.getDetail( Mod303Key.C65 ).getAmount();
		double c66 = CommonUtil.round(c64 * c65 / 100 );
		mod303.getDetail( Mod303Key.C66 ).setAmount( c66 );
		double c67 = mod303.getDetail( Mod303Key.C67 ).getAmount();
		double c68 = mod303.getDetail( Mod303Key.C68 ).getAmount();
		double c69 = CommonUtil.round(c66 - c67 + c68);
		mod303.getDetail( Mod303Key.C69 ).setAmount( c69 );
		double c70 = mod303.getDetail( Mod303Key.C70 ).getAmount();
		double c71 = CommonUtil.round(c69 - c70);
		mod303.getDetail( Mod303Key.C71 ).setAmount( c71 );
	}

	@Override
	public double getResult(Mod303 mod303) {
		return mod303.getDetail( Mod303Key.C71).getAmount();
	}

}
