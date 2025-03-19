package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import java.util.LinkedList;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.impl.jooq.dao.ModelMVELContext;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod202MVELContext extends ModelMVELContext implements Map<String, Object> {
	public static final String X08_1 = "202-X08-1";
	public static final String X08_2 = "202-X08-2";
	public static final String X08_3 = "202-X08-3"; // Porcentaje 3 de la modalidad B2 (se utiliza a partir de 2025)
	public static final String X08_4 = "202-X08-4"; // Porcentaje 4 de la modalidad B2 (se utiliza a partir de 2025)
	
	protected Mod202 mod202;
	protected final LinkedList<Mod202Key> keys = new LinkedList<>();
	
	public Mod202MVELContext(final Mod202 mod202) {
		this.mod202 = mod202;
		for (String keyValue : mod202.getMap().keySet()) {
			Mod202Key mod202Key = Mod202Key.getKey(keyValue);
			if (mod202Key != null) {
				FiscalModelDetail detail = mod202.getMap().get(keyValue);
				put(mod202Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
	}
	
	public boolean isMethodA() 	{ return mod202.isMethodA();}
	public boolean isMethodB() 	{ return mod202.isMethodB();}
	public boolean isMethodB1() { return mod202.isMethodB1();}
	public boolean isMethodB2() { return mod202.isMethodB2();}

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
	public double getPercent3() {
		Double x08 = (Double) get(X08_3);
		return x08;
	}
	public double getPercent4() {
		Double x08 = (Double) get(X08_4);
		return x08;
	}
	
	public boolean isX04Empty() {
		Double x04 = (Double) get(Mod202Key.X04.toString());
		return x04 == 0;
	}
	
	public double computeC17() {
		double x08 = getPercent();
		double c17;		
		if (!isX04Empty() && mod202.getYear() >= 2017) {
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

	// Casilla [21] Porcentaje 1 de la Modalidad B2
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

	// Casilla [24] Porcentaje 2 de la Modalidad B2
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
	
	// FALTA - POR AHORA EL CALCULO DEL PORCENTAJE 3 SE HACE IGUAL QUE LOS DOS PRIMEROS PORCENTAJES
	// Casilla [62] Porcentaje 3 de la Modalidad B2
	public double computeC62() {
		double x08 = getPercent3();
		double c62 = 0.0;
		Double c61 = (Double) get(Mod202Key.C61.toString());
		if (c61 != 0) {
			if (isX09Empty()) {
				c62 = AonMathUtils.floor((5.0/7.0) * x08,0);	
			} else {
				c62 = AonMathUtils.ceil((19.0/20.0) * x08,0);
			}
		}
		return c62;
	}
	
	// FALTA - POR AHORA EL CALCULO DEL PORCENTAJE 4 SE HACE IGUAL QUE LOS DOS PRIMEROS PORCENTAJES
	// Casilla [65] Porcentaje 4 de la Modalidad B2
	public double computeC65() {
		double x08 = getPercent4();
		double c65 = 0.0;
		Double c64 = (Double) get(Mod202Key.C64.toString());
		if (c64 != 0) {
			if (isX09Empty()) {
				c65 = AonMathUtils.floor((5.0/7.0) * x08,0);	
			} else {
				c65 = AonMathUtils.ceil((19.0/20.0) * x08,0);
			}
		}
		return c65;
	}
	
	public double computeC32() {
		if ( isMethodA() ) return 0;
		double c18 = (Double) get(Mod202Key.C18.toString());
		double c26 = (Double) get(Mod202Key.C26.toString());
		double c27 = (Double) get(Mod202Key.C27.toString());
		double c28 = (Double) get(Mod202Key.C28.toString());
		double c29 = (Double) get(Mod202Key.C29.toString());
		double c30 = (Double) get(Mod202Key.C30.toString());
		double c31 = (Double) get(Mod202Key.C31.toString());
		double prevResult = isMethodB1()?c18:c26;
		double c32 = ((prevResult-c27-c28)*c29/100)-c30-c31;
		if ( c32 < 0 ) c32 = 0.0;
		return c32;
	}
	
	
}
