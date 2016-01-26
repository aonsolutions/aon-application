package com.code.aon.fiscal.mod303;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Aeat2014Mod303Calculator extends FiscalModelDetailCalculator implements IMod303Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2014 && administration == Administration.COMMON_TERRITORY) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod303 mod303) throws AonException {
		calculateDetails(mod303.getDetails());

		if (mod303.getDetail(Mod303Key.CAG1) != null) {
			double g1 = mod303.ensureAmount( Mod303Key.CAG1_V1);
			double g2 = mod303.ensureAmount( Mod303Key.CAG1_V2);
			if (g2 > 100) {
				g2 = CommonUtil.round((g2 / 10000),5);
			}
			double g3 = CommonUtil.round(g1 * g2);
			mod303.getDetail( Mod303Key.CAG1_V3).setAmount( g3 );
			if (mod303.isLastPeriod()) {
				double g6 = mod303.ensureAmount( Mod303Key.CAG1_V6);
				double g7 = CommonUtil.round(g3 - g6);
				mod303.getDetail( Mod303Key.CAG1_V7).setAmount( g7 );	
			} else {
				double g4 = mod303.ensureAmount( Mod303Key.CAG1_V4);
				double g5 = CommonUtil.round(g3 * g4 / 100);
				mod303.getDetail( Mod303Key.CAG1_V5).setAmount( g5 );
			}
		}

		if (mod303.getDetail(Mod303Key.CAG2) != null) {
			double g1 = mod303.ensureAmount( Mod303Key.CAG2_V1);
			double g2 = mod303.ensureAmount( Mod303Key.CAG2_V2);
			if (g2 > 100) {
				g2 = CommonUtil.round((g2 / 10000),4);
			}
			double g3 = CommonUtil.round(g1 * g2);
			mod303.getDetail( Mod303Key.CAG2_V3).setAmount( g3 );
			if (mod303.isLastPeriod()) {
				double g6 = mod303.ensureAmount( Mod303Key.CAG2_V6);
				double g7 = CommonUtil.round(g3 - g6);
				mod303.getDetail( Mod303Key.CAG2_V7).setAmount( g7 );
			}
		}
		if (mod303.getDetail(Mod303Key.CAG3) != null) {
			double g1 = mod303.ensureAmount( Mod303Key.CAG3_V1);
			double g2 = mod303.ensureAmount( Mod303Key.CAG3_V2);
			if (g2 > 100) {
				g2 = CommonUtil.round((g2 / 10000),4);
			}
			double g3 = CommonUtil.round(g1 * g2);
			mod303.getDetail( Mod303Key.CAG3_V3).setAmount( g3 );
			if (mod303.isLastPeriod()) {
				double g6 = mod303.ensureAmount( Mod303Key.CAG3_V6);
				double g7 = CommonUtil.round(g3 - g6);
				mod303.getDetail( Mod303Key.CAG3_V7).setAmount( g7 );
			}
		}
		if (mod303.getDetail(Mod303Key.CAG4) != null) {
			double g1 = mod303.ensureAmount( Mod303Key.CAG4_V1);
			double g2 = mod303.ensureAmount( Mod303Key.CAG4_V2);
			if (g2 > 100) {
				g2 = CommonUtil.round((g2 / 10000),4);
			}
			double g3 = CommonUtil.round(g1 * g2);
			mod303.getDetail( Mod303Key.CAG4_V3).setAmount( g3 );
			if (mod303.isLastPeriod()) {
				double g6 = mod303.ensureAmount( Mod303Key.CAG4_V6);
				double g7 = CommonUtil.round(g3 - g6);
				mod303.getDetail( Mod303Key.CAG4_V7).setAmount( g7 );
			}
		}
		
		if (mod303.getDetail(Mod303Key.CAC1) != null) {
			if (!mod303.isLastPeriod()) {
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
			} else {
				Mod303Key[] units = new Mod303Key[] {
						Mod303Key.CAC1_M1U,Mod303Key.CAC1_M2U
						,Mod303Key.CAC1_M3U,Mod303Key.CAC1_M4U
							,Mod303Key.CAC1_M5U,Mod303Key.CAC1_M6U
						,Mod303Key.CAC1_M7U};
				Mod303Key[] modules = new Mod303Key[] {
						Mod303Key.CAC1_M1I,Mod303Key.CAC1_M2I
						,Mod303Key.CAC1_M3I,Mod303Key.CAC1_M4I
						,Mod303Key.CAC1_M5I,Mod303Key.CAC1_M6I
						,Mod303Key.CAC1_M7I};
				
				double ht1 = mod303.ensureAmount( Mod303Key.CAC1_HT);
				double hd1 = mod303.ensureAmount( Mod303Key.CAC1_HD);
				
				for (int i = 0; i < units.length ; i++) {
					Mod303Key mod = units[i];
					if (mod303.getDetail( mod ) != null) {
						double u1 = mod303.ensureAmount( mod );
						double or = mod303.getDetail( mod ).getDeclaredAmount();
						u1 = CommonUtil.round( (ht1 + hd1) * or / 360);
						mod303.getDetail( mod ).setAmount( u1 );
						
						FiscalModelDetail det = mod303.getDetail( modules[i] );
						det.setAmount( CommonUtil.round( det.getDeclaredAmount() * u1 ) );
					}
				}
				
				double c1 = 0.0;
				for (int i = 0; i < modules.length ; i++) {
					if (mod303.getDetail( modules[i] ) != null) {
						c1 = CommonUtil.round(c1 + mod303.getDetail( modules[i] ).getAmount());
					}
				}
				mod303.getDetail( Mod303Key.CAC1_C ).setAmount( c1 );
				
				double g01 = CommonUtil.round( (c1 * 1 / 100));
				mod303.ensureDetail( Mod303Key.CAC1_G0 ).setAmount( g01 );
				
				double d1 = mod303.ensureAmount( Mod303Key.CAC1_D);
				double g1 = mod303.ensureAmount( Mod303Key.CAC1_G);
				double h1 = mod303.ensureAmount( Mod303Key.CAC1_H);
				double i1 = CommonUtil.round( ((c1 - d1 - g01 - g1)));
				if (h1 != 0) {
					i1 = CommonUtil.round( i1 * h1 );
				}
				mod303.getDetail( Mod303Key.CAC1_I ).setAmount( i1 );
				
				double j1 = mod303.ensureAmount( Mod303Key.CAC1_J);
				double k1 = mod303.ensureAmount( Mod303Key.CAC1_K);
				double l1 = CommonUtil.round( ((c1 - d1) * j1 / 100) + k1);
				if (h1 != 0) {
					l1 = CommonUtil.round( l1 * h1 );
				}
				mod303.getDetail( Mod303Key.CAC1_L ).setAmount( l1 );
				
				mod303.getDetail( Mod303Key.CAC1_M ).setAmount( l1>i1?l1:i1 );
			}
		}
		
		if (mod303.getDetail(Mod303Key.CAC2) != null) {
			if (!mod303.isLastPeriod()) {
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
			} else {
				Mod303Key[] units = new Mod303Key[] {
						Mod303Key.CAC2_M1U,Mod303Key.CAC2_M2U
						,Mod303Key.CAC2_M3U,Mod303Key.CAC2_M4U
						,Mod303Key.CAC2_M5U,Mod303Key.CAC2_M6U
						,Mod303Key.CAC2_M7U};
				Mod303Key[] modules = new Mod303Key[] {
						Mod303Key.CAC2_M1I,Mod303Key.CAC2_M2I
						,Mod303Key.CAC2_M3I,Mod303Key.CAC2_M4I
						,Mod303Key.CAC2_M5I,Mod303Key.CAC2_M6I
						,Mod303Key.CAC2_M7I};
				
				double ht2 = mod303.ensureAmount( Mod303Key.CAC2_HT);
				double hd2 = mod303.ensureAmount( Mod303Key.CAC2_HD);
				
				for (int i = 0; i < units.length ; i++) {
					Mod303Key mod = units[i];
					if (mod303.getDetail( mod ) != null) {
						double u2 = mod303.ensureAmount( mod );
						double or = mod303.getDetail( mod ).getDeclaredAmount();
						u2 = CommonUtil.round( (ht2 + hd2) * or / 360);
						mod303.getDetail( mod ).setAmount( u2 );
						
						FiscalModelDetail det = mod303.getDetail( modules[i] );
						det.setAmount( CommonUtil.round( det.getDeclaredAmount() * u2 ) );
					}
				}
				
				double c2 = 0.0;
				for (int i = 0; i < modules.length ; i++) {
					if (mod303.getDetail( modules[i] ) != null) {
						c2 = CommonUtil.round(c2 + mod303.getDetail( modules[i] ).getAmount());
					}
				}
				mod303.getDetail( Mod303Key.CAC2_C ).setAmount( c2 );
				double g02 = CommonUtil.round( (c2 * 1 / 100));
				mod303.ensureDetail( Mod303Key.CAC2_G0 ).setAmount( g02 );
				double d2 = mod303.ensureAmount( Mod303Key.CAC2_D);
				double g2 = mod303.ensureAmount( Mod303Key.CAC2_G);
				double h2 = mod303.ensureAmount( Mod303Key.CAC2_H);
				double i2 = CommonUtil.round( (c2 - d2 - g02 - g2));
				if (h2 != 0) {
					i2 = CommonUtil.round( i2 * h2 );
				}
				mod303.getDetail( Mod303Key.CAC2_I ).setAmount( i2 );
				
				double j2 = mod303.ensureAmount( Mod303Key.CAC2_J);
				double k2 = mod303.ensureAmount( Mod303Key.CAC2_K);
				double l2 = CommonUtil.round( ((c2 - d2) * j2 / 100) + k2);
				if (h2 != 0) {
					l2 = CommonUtil.round( l2 * h2 );
				}
				mod303.getDetail( Mod303Key.CAC2_L ).setAmount( l2 );
				
				mod303.getDetail( Mod303Key.CAC2_M ).setAmount( l2>i2?l2:i2 );
			}
		}
		
		if (mod303.getDetail(Mod303Key.CAC3) != null) {
			if (!mod303.isLastPeriod()) {
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
			} else {
				Mod303Key[] units = new Mod303Key[] {
						Mod303Key.CAC3_M1U,Mod303Key.CAC3_M1U
						,Mod303Key.CAC3_M3U,Mod303Key.CAC3_M4U
						,Mod303Key.CAC3_M5U,Mod303Key.CAC3_M6U
						,Mod303Key.CAC3_M7U};
				Mod303Key[] modules = new Mod303Key[] {
						Mod303Key.CAC3_M2I,Mod303Key.CAC3_M2I
						,Mod303Key.CAC3_M3I,Mod303Key.CAC3_M4I
						,Mod303Key.CAC3_M5I,Mod303Key.CAC3_M6I
						,Mod303Key.CAC3_M7I};
				
				double ht3 = mod303.ensureAmount( Mod303Key.CAC3_HT);
				double hd3 = mod303.ensureAmount( Mod303Key.CAC3_HD);
				
				for (int i = 0; i < units.length ; i++) {
					Mod303Key mod = units[i];
					if (mod303.getDetail( mod ) != null) {
						double u3 = mod303.ensureAmount( mod );
						double or = mod303.getDetail( mod ).getDeclaredAmount();
						u3 = CommonUtil.round( (ht3 + hd3) * or / 360);
						mod303.getDetail( mod ).setAmount( u3 );
						
						FiscalModelDetail det = mod303.getDetail( modules[i] );
						det.setAmount( CommonUtil.round( det.getDeclaredAmount() * u3 ) );
					}
				}
				
				double c3 = 0.0;
				for (int i = 0; i < modules.length ; i++) {
					if (mod303.getDetail( modules[i] ) != null) {
						c3 = CommonUtil.round(c3 + mod303.getDetail( modules[i] ).getAmount());
					}
				}
				mod303.getDetail( Mod303Key.CAC3_C ).setAmount( c3 );
				double g03 = CommonUtil.round( (c3 * 1 / 100));
				mod303.ensureDetail( Mod303Key.CAC3_G0 ).setAmount( g03 );
				double d3 = mod303.ensureAmount( Mod303Key.CAC3_D);
				double g3 = mod303.ensureAmount( Mod303Key.CAC3_G);
				double i3 = CommonUtil.round( (c3 - d3 - g03 - g3));
				mod303.getDetail( Mod303Key.CAC3_I ).setAmount( i3 );
				
				double j3 = mod303.ensureAmount( Mod303Key.CAC3_J);
				double k3 = mod303.ensureAmount( Mod303Key.CAC3_K);
				double l3 = CommonUtil.round( ((c3 - d3) * j3 / 100) + k3);
				mod303.getDetail( Mod303Key.CAC3_L ).setAmount( l3 );
				
				mod303.getDetail( Mod303Key.CAC3_M ).setAmount( l3>i3?l3:i3 );
			}
		}
		
		if (mod303.getDetail(Mod303Key.CAC4) != null) {
			if (!mod303.isLastPeriod()) {
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
			} else {
				Mod303Key[] units = new Mod303Key[] {
						Mod303Key.CAC4_M1U,Mod303Key.CAC4_M1U
						,Mod303Key.CAC4_M3U,Mod303Key.CAC4_M4U
						,Mod303Key.CAC4_M5U,Mod303Key.CAC4_M6U
						,Mod303Key.CAC4_M7U};
				Mod303Key[] modules = new Mod303Key[] {
						Mod303Key.CAC4_M2I,Mod303Key.CAC4_M2I
						,Mod303Key.CAC4_M3I,Mod303Key.CAC4_M4I
						,Mod303Key.CAC4_M5I,Mod303Key.CAC4_M6I
						,Mod303Key.CAC4_M7I};
				
				double ht4 = mod303.ensureAmount( Mod303Key.CAC4_HT);
				double hd4 = mod303.ensureAmount( Mod303Key.CAC4_HD);
				
				for (int i = 0; i < units.length ; i++) {
					Mod303Key mod = units[i];
					if (mod303.getDetail( mod ) != null) {
						double u4 = mod303.ensureAmount( mod );
						double or = mod303.getDetail( mod ).getDeclaredAmount();
						u4 = CommonUtil.round( (ht4 + hd4) * or / 360);
						mod303.getDetail( mod ).setAmount( u4 );
						
						FiscalModelDetail det = mod303.getDetail( modules[i] );
						det.setAmount( CommonUtil.round( det.getDeclaredAmount() * u4 ) );
					}
				}
				
				double c4 = 0.0;
				for (int i = 0; i < modules.length ; i++) {
					if (mod303.getDetail( modules[i] ) != null) {
						c4 = CommonUtil.round(c4 + mod303.getDetail( modules[i] ).getAmount());
					}
				}
				mod303.getDetail( Mod303Key.CAC3_C ).setAmount( c4 );
				double g04 = CommonUtil.round( (c4 * 1 / 100));
				mod303.ensureDetail( Mod303Key.CAC4_G0 ).setAmount( g04 );
				double d4 = mod303.ensureAmount( Mod303Key.CAC4_D);
				double g4 = mod303.ensureAmount( Mod303Key.CAC4_G);
				double i4 = CommonUtil.round( (c4 - d4 - g04 - g4));
				mod303.getDetail( Mod303Key.CAC4_I ).setAmount( i4 );
				
				double j4 = mod303.ensureAmount( Mod303Key.CAC4_J);
				double k4 = mod303.ensureAmount( Mod303Key.CAC4_K);
				double l4 = CommonUtil.round( ((c4 - d4) * j4 / 100) + k4);
				mod303.getDetail( Mod303Key.CAC4_L ).setAmount( l4 );
				
				mod303.getDetail( Mod303Key.CAC4_M ).setAmount( l4>i4?l4:i4 );
			}
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
			mod303.getDetail( Mod303Key.C48 ).setAmount( CommonUtil.round(c48) );
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
		
		double c80 = mod303.ensureDetail( Mod303Key.C80 ).getAmount();
		double c81 = mod303.ensureDetail( Mod303Key.C81 ).getAmount();
		double c82 = mod303.ensureDetail( Mod303Key.C82 ).getAmount();
		double c83 = mod303.ensureDetail( Mod303Key.C83 ).getAmount();
		double c84 = mod303.ensureDetail( Mod303Key.C84 ).getAmount();
		double c85 = mod303.ensureDetail( Mod303Key.C85 ).getAmount();
		double c86 = mod303.ensureDetail( Mod303Key.C86 ).getAmount();
		double c87 = mod303.ensureDetail( Mod303Key.C87 ).getAmount();
		double c88 = CommonUtil.round(c80 + c81 + c82 + c83 + c84 + c85 + c86 - c87);
		mod303.ensureDetail( Mod303Key.C88 ).setAmount( c88 );
	}

	@Override
	public double getResult(Mod303 mod303) {
		return mod303.getDetail( Mod303Key.C71).getAmount();
	}

}
