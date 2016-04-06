package com.esferalia.aon.occam.server.fiscal.calc;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Aeat2016Mod202Calculator {

	public static Mod202 calculate(AONContext ctx, Mod202 mod202) {
		int year = mod202.getInitialDate() == null?2016: AonDateUtils.getYear( mod202.getInitialDate() );
		double x00 = mod202.getAmount(Mod202Key.X00);
		if (x00 == 0) {
			double c01 = mod202.getAmount(Mod202Key.C01);
			double c02 = mod202.getAmount(Mod202Key.C02);
			double c03 = AonMathUtils.round( (c01 * 18 / 100) - c02);
			mod202.putAmount(Mod202Key.C03, c03);
			for (Mod202Key key : Mod202Key.MOD_B_KEYS) {
				mod202.putAmount(key, 0);	
			}
			for (Mod202Key key : Mod202Key.MOD_B1_KEYS) {
				mod202.putAmount(key, 0);	
			}
			for (Mod202Key key : Mod202Key.MOD_B2_KEYS) {
				mod202.putAmount(key, 0);	
			}
		} else {
			double c05 = mod202.getAmount(Mod202Key.C05);
			double c07 = mod202.getAmount(Mod202Key.C07);
			double c38 = AonMathUtils.round(c05 + c07);
			mod202.putAmount(Mod202Key.C38, c38);
			
			double c06 = mod202.getAmount(Mod202Key.C06);
			double c37 = mod202.getAmount(Mod202Key.C37);
			double c08 = mod202.getAmount(Mod202Key.C08);
			double c39 = AonMathUtils.round(c06 + c37 + c08);
			mod202.putAmount(Mod202Key.C39, c39);
			
			double c04 = mod202.getAmount(Mod202Key.C04);
			double c09 = mod202.getAmount(Mod202Key.C09);
			double c43 = mod202.getAmount(Mod202Key.C43);
			double c13 = AonMathUtils.round(c04+c38-c39+c09+c43);
			mod202.putAmount(Mod202Key.C13, c13);
			
			if (c13<=0) {
				mod202.putAmount(Mod202Key.C14, 0.0);	
			}
			double c44 = mod202.getAmount(Mod202Key.C44);
			double c14 = mod202.getAmount(Mod202Key.C14);
			double c45 = mod202.getAmount(Mod202Key.C45);
			double c46 = mod202.getAmount(Mod202Key.C46);

			boolean x06 = mod202.getCheck(Mod202Key.X06);
			double x09 = mod202.getAmount(Mod202Key.X09);
			double c18o26 = 0.0;
			if (x00 == 1) {
				String s08 = mod202.getDescription(Mod202Key.X08);
				s08 = AonStringUtils.substringBefore(s08, "/");
				double x08 = AonNumberUtils.todouble(s08);
				double c16 = AonMathUtils.round(c13-c44-c14+c45-c46);
				c16 = c16<0?0:c16;
				mod202.putAmount(Mod202Key.C16, c16);
				
				double c17 = 0.0;
				if ( year == 2016 ) {
					if (!x06) {
						c17 = AonMathUtils.round(5.0/7.0 * x08,0);
					} else {
						if (x09 == 1) {
							c17 = AonMathUtils.round(15.0/20.0 * x08,0);
						} else if (x09 == 2) {
							c17 = AonMathUtils.round(17.0/20.0 * x08,0);
						} else if (x09 == 3) {
							c17 = AonMathUtils.round(19.0/20.0 * x08,0);
						} else {
							c17 = AonMathUtils.round(5.0/7.0 * x08,0);
						}
					}
					mod202.putAmount(Mod202Key.C17, c17);	
				}
				double c47 = mod202.getAmount(Mod202Key.C47);
				double c40 = mod202.getAmount(Mod202Key.C40);
				double c48 = mod202.getAmount(Mod202Key.C48);
				double c49 = mod202.getAmount(Mod202Key.C49);
				double c18 = AonMathUtils.round( (c16*c17/100) +c47-c40+c48-c49);
				c18o26 = c18;
				mod202.putAmount(Mod202Key.C18, c18);
				
				for (Mod202Key key : Mod202Key.MOD_A_KEYS) {
					mod202.putAmount(key, 0);	
				}
				for (Mod202Key key : Mod202Key.MOD_B2_KEYS) {
					mod202.putAmount(key, 0);	
				}
				
			} else {
				
				double c19 = AonMathUtils.round( c13-c44-c14+c45-c46  );
				c19 = c19<0?0:c19;
				mod202.putAmount(Mod202Key.C19, c19);

				
				String percent = mod202.getDescription(Mod202Key.X08);
				double x08_1 = AonNumberUtils.todouble(AonStringUtils.substringBefore(percent, "/"));
				double x08_2 = AonNumberUtils.todouble(AonStringUtils.substringAfter(percent, "/"));
				double c21 = 0.0;
				if ( year == 2016 ) {
					if (!x06) {
						c21 = AonMathUtils.floor(5.0/7.0 * x08_1, 0);						
					} else {
						if (x09 == 1) {
							c21 = AonMathUtils.round(15.0/20.0 * x08_1,0);
						} else if (x09 == 2) {
							c21 = AonMathUtils.round(17.0/20.0 * x08_1,0);
						} else if (x09 == 3) {
							c21 = AonMathUtils.round(19.0/20.0 * x08_1,0);
						} else {
							c21 = AonMathUtils.round(5.0/7.0 * x08_1,0);
						}
					}
					mod202.putAmount(Mod202Key.C21, c21);	
				}
				
				double c20 = mod202.getAmount(Mod202Key.C20);
				double c22 = AonMathUtils.round(c20*c21/100);
				mod202.putAmount(Mod202Key.C22, c22);
				
				double c23 = AonMathUtils.round(c19-c20);
				mod202.putAmount(Mod202Key.C23, c23);

				double c24 = 0.0;
				if ( year == 2016 ) {
					if (!x06) {
						c24 = AonMathUtils.floor(5.0/7.0 * x08_2,0);
					} else {
						if (x09 == 1) {
							c24 = AonMathUtils.round(15.0/20.0 * x08_2,0);
						} else if (x09 == 2) {
							c24 = AonMathUtils.round(17.0/20.0 * x08_2,0);
						} else if (x09 == 3) {
							c24 = AonMathUtils.round(19.0/20.0 * x08_2,0);
						} else {
							c24 = AonMathUtils.round(5.0/7.0 * x08_2,0);
						}
					}
					mod202.putAmount(Mod202Key.C24, c24);
					
					double c25 = AonMathUtils.round(c23*c24/100);
					mod202.putAmount(Mod202Key.C25, c25);

					double c50 = mod202.getAmount(Mod202Key.C50);
					double c42 = mod202.getAmount(Mod202Key.C42);
					double c51 = mod202.getAmount(Mod202Key.C51);
					double c52 = mod202.getAmount(Mod202Key.C52);
					double c26 = AonMathUtils.round(c22+c25+c50-c42+c51-c52);
					c18o26 = c26;
					mod202.putAmount(Mod202Key.C26, c26);
					
					for (Mod202Key key : Mod202Key.MOD_A_KEYS) {
						mod202.putAmount(key, 0);	
					}
					for (Mod202Key key : Mod202Key.MOD_B1_KEYS) {
						mod202.putAmount(key, 0);	
					}
				}
			}
			double c27 = mod202.getAmount(Mod202Key.C27);
			double c28 = mod202.getAmount(Mod202Key.C28);
			double c29 = mod202.getAmount(Mod202Key.C29);
			double c30 = mod202.getAmount(Mod202Key.C30);
			double c31 = mod202.getAmount(Mod202Key.C31);
			double c32 = AonMathUtils.round(((c18o26 - c27 -c28) * c29 / 100) - c30 -c31);
			mod202.putAmount(Mod202Key.C32, c32);
			
			double c34 = c32;
			mod202.putAmount(Mod202Key.C34, c34);
		}
		return mod202;
	}

}
