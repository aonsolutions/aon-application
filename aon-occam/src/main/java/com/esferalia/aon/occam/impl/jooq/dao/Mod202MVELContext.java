package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Map;

import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod202MVELContext extends ModelMVELContext implements Map<String, Object> {
	public static final String X08_1 = "202-X08-1";
	public static final String X08_2 = "202-X08-2";
	
	public int year;
	
	public boolean isMethodA() {
		Double x00 = (Double) get(Mod202Key.X00.toString());
		return x00 == 0;
	}
	public boolean isMethodB() {
		Double x00 = (Double) get(Mod202Key.X00.toString());
		return x00 != 0;
	}
	public boolean isMethodB1() {
		Double x00 = (Double) get(Mod202Key.X00.toString());
		return x00 == 1;
	}
	public boolean isMethodB2() {
		Double x00 = (Double) get(Mod202Key.X00.toString());
		return x00 == 2;
	}
	public boolean isX09Empty() {
		Double x09 = (Double) get(Mod202Key.X09.toString());
		return x09 == 0;
	}
	public double getPercent() {
		Double x08 = (Double) get(Mod202Key.X08.toString());
		return x08;
	}
	public double getPercent1() {
		Double x08 = (Double) get(X08_1);
		return x08;
	}
	public double getPercent2() {
		Double x08 = (Double) get(X08_2);
		return x08;
	}
	
	public boolean isX04Empty() {
		Double x04 = (Double) get(Mod202Key.X04.toString());
		return x04 == 0;
	}
	
	public double computeC17() {
		double x08 = getPercent();
		double c17;		
		if (!isX04Empty() &&  year >= 2017  ) {
			// A partir del 2017 si está marcado lo de las entidades navieras, el porcentaje es del 25%
			c17 = 25;
		} 
		else if (isX09Empty()) {
			c17 = AonMathUtils.floor((5.0/7.0) * x08,0);	
		} else {
			c17 = AonMathUtils.ceil((19.0/20.0) * x08,0);
		}
		return c17;
	}

	public double computeC21() {
		double x08 = getPercent1();
		Double c20 = (Double) get(Mod202Key.C20.toString());
		double c21 = 0.0;
		if (c20 != 0.0) {
			if (isX09Empty()) {
				c21 = AonMathUtils.floor((5.0/7.0) * x08,0);	
			} else {
				c21 = AonMathUtils.ceil((19.0/20.0) * x08,0);
			}
		}
		return c21;
	}

	public double computeC24() {
		double x08 = getPercent2();
		double c24 = 0.0;
		Double c23 = (Double) get(Mod202Key.C23.toString());
		if (c23 != 0) {
			if (isX09Empty()) {
				c24 = AonMathUtils.floor((5.0/7.0) * x08,0);	
			} else {
				c24 = AonMathUtils.ceil((19.0/20.0) * x08,0);
			}
		}
		return c24;
	}
}
