package com.esferalia.aon.occam.server.fiscal.calc;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131.Mod131Activity;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Aeat2013Mod131Calculator {

	public static Mod131 calculate(AONContext ctx, Mod131 mod131) {
		double c01 = 0;
		for (Mod131Activity act : mod131.getActivities()) {
			c01 =  AonMathUtils.round(c01  + act.getNetYield());	
		}
		mod131.setC01( c01 );
		double c02 = mod131.getC02();
		double c03 = mod131.getC03();
		double c04 =  AonMathUtils.round(c03 * 2 / 100);
		mod131.setC04( c04 );
		double c05 = mod131.getC05();
		double c06 =  AonMathUtils.round(c05 * 2 / 100);
		mod131.setC06( c06 );
		double c07 =  AonMathUtils.round(c02 + c04 + c06 );
		mod131.setC07( c07 );
		double c08 = mod131.getC08();
		double c09 = mod131.getC09();
		double c10 =  AonMathUtils.round(c07 - c08 - c09 );
		mod131.setC10( c10 );		
		double c11 =  mod131.getC11();
		double c12 =  mod131.getC12();
		double c13 =  AonMathUtils.round(c10 - c11 - c12 );
		mod131.setC13( c13 );
		double c14 =  mod131.getC14();
		double c15 =  AonMathUtils.round(c13 - c14 );
		mod131.setC15( c15 );
		return mod131;
	}

}
