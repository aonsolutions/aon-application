package com.esferalia.aon.occam.impl.jooq.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModulesCanarias2026;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421MVELContext extends ModelMVELContext implements Map<String, Object> {
	
//	public static final int A1IDX = 0; // Actividad 1
	
	private Mod421 mod421;
	
	public Mod421MVELContext(Mod421 mod421) {
		this.mod421 = mod421;
	}
	public Mod421 getMod421() {
		return mod421;
	}
	public boolean isLastPeriod() {
		return this.mod421.getPeriod() == Period.T4;
	}
//	public boolean isDevReg() {
//		return this.mod421.isDevReg();
//	}
	public boolean isToCompensate() {
		return this.mod421.isToCompensate();
	}
	public boolean isToDeposit() {
		return this.mod421.isToDeposit();
	}
	public boolean isToPayback() {
		return this.mod421.isToPayback();
	}

//	public boolean hasSimplifiedRegime() {
//		Double a02 = (Double) get(Mod421Key.CT_A02.toString());
//		return a02 != 2;
//	}
	
//	public boolean hasFarmerActivity(int idx) {
//		return hasSimplifiedRegime() 
//			&& this.mod421.getActivityFarmerList() != null 
//			&& idx < this.mod421.getActivityFarmerList().size()
//			&& this.mod421.getActivityFarmerList().get(idx) != null
//			&& AonStringUtils.isNotBlank( this.mod421.getActivityFarmerList().get(idx).getCode() )
//			;
//	}
	
	public boolean hasActivity(int idx) {
		return 
//				hasSimplifiedRegime() 
//			&& 
			this.mod421.getActivityList() != null 
			&& idx < this.mod421.getActivityList().size()
			&& this.mod421.getActivityList().get(idx) != null
			&& AonStringUtils.isNotBlank( this.mod421.getActivityList().get(idx).getEpigraph() )
			;
	}
	
//	public double calculateReduccion(int epigraph,double cuota,double lorca,double covid) {
//		double red = 0.0;
//		if ( AonMathUtils.equals(1, lorca)) {
//			red = AonMathUtils.round(red + (cuota * 20 / 100));
//		}
//		if ( AonMathUtils.equals(1, covid)) {
//			String epi = this.mod421.getActivityList().get(epigraph).getEpigraph();
//			double percent =  
//				("653.2".equals(epi) || "653.4".equals(epi) ||
//				"653.5".equals(epi)  || "654.2".equals(epi) ||
//				"654.5".equals(epi)  || "654.6".equals(epi) ||
//				"659.3".equals(epi)  || "663.1".equals(epi) ||
//				"671.4".equals(epi)  || "671.5".equals(epi) ||
//				"672.1".equals(epi)  || "672.2".equals(epi) ||
//				"672.3".equals(epi)  || "673.1".equals(epi) ||
//				"673.2".equals(epi)  || "675".equals(epi) ||
//				"676".equals(epi) 	 || "681".equals(epi) ||
//				"682".equals(epi) 	 || "683".equals(epi) ||
//				"721.1".equals(epi)  || "721.3".equals(epi))
//			? 35.0
//			: 20.0;
//			red = AonMathUtils.round(red + (cuota * percent / 100));		
//		}
//		return red;
//	}

//	public double calculateReduccion2021(int epigraph,double cuota,double lorca,double covid) {
//		double red = 0.0;
//		if ( AonMathUtils.equals(1, lorca)) {
//			red = AonMathUtils.round(red + (cuota * 20 / 100));
//		}
//		return red;
//	}
	
	// Cálculo reducción (Lorca o DANA), a partir del 4T del 2024
//	public double calculateReduction2024(int type, double percent, double cuota, double reduction) {
//		if (type == 1) // En exclusiva 
//			return AonMathUtils.round(cuota * percent / 100);
//		else if (type == 2) // En municipios de la reducción y otros municipios (se devuelve lo que haya introducido el usuario)
//			return reduction;
//		else 
//			return 0.0; // No aplicable
//	}

	public double calculateIndiceTemporada(Double dias) {
		if (dias != null) {
			if (dias > 0 && dias <= 60) return 1.50;
			if (dias > 60 && dias <= 120) return 1.35;
			if (dias > 120) return 1.25;
		}
		return 0;
	}
	
	public double calculatePorcentajeIngresoCuenta(int actIdx) {
		if (isLastPeriod()) 
			return 0.0;
		String epigraph = this.mod421.getActivityList().get(actIdx).getEpigraph();
		int specialEpigraph = this.mod421.getActivityList().get(actIdx).getSpecialEpigraph();
		ModulesCanarias2026.EpigraphCanarias epi = ModulesCanarias2026.EpigraphCanarias.getEpigraph(epigraph, specialEpigraph);
		double por = 0.0;
		if (epi != null) 
			por = epi.getVatPorc();
		return por;
	}

//	public double calculateIngresoCuenta(int actNum, double daysAct, double daysTrim, double quota, double reductions, double tempIndex, double percent) {
//		if (isLastPeriod())
//			return 0.0;
//		double diasActividad = 0;
//		if (tempIndex == 0) {
//			tempIndex = 1;
//			diasActividad = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod421), FiscalUtils.getPeriodEnd(mod421)) + 1;
//		} else {
//			diasActividad = daysAct;
//		}
//
//		double f1 = (quota - reductions) * percent / 100;
//		f1 = f1 * tempIndex;
//		f1 = AonMathUtils.round(f1 * daysTrim / diasActividad);
//		return f1;
//	}
	
	public double calculateIngresoCuenta(int actNum, double daysAct, double daysTrim, double tempIndex, double percent) {
		if (isLastPeriod())
			return 0.0;
		double diasActividad = 0;
		if (tempIndex == 0) {
			tempIndex = 1;
			diasActividad = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod421), FiscalUtils.getPeriodEnd(mod421)) + 1.0;
		} else {
			diasActividad = daysAct;
		}
		double q = 0.0;
		double comisiones = 0.0;
		for (int i = 0; i < 7; i++ ) {
			double result = this.mod421.getActivityList().get(actNum).getModules().get(i).getResult();
			if (AonMathUtils.isZero(result)) {
				double value = this.mod421.getActivityList().get(actNum).getModules().get(i).getValue();
				double factor = this.mod421.getActivityList().get(actNum).getModules().get(i).getFactor();
				result = AonMathUtils.round(value * factor);
			}
			String desc   = this.mod421.getActivityList().get(actNum).getModules().get(i).getDescription();
			if ( AonStringUtils.isNotBlank(desc) && AonStringUtils.containsIgnoreCase(desc, "comisiones")) {
				comisiones = result;
			} else {
				q = q + result; 
			}
		}
		double f1 = ((q) * percent / 100) + comisiones;
		f1 = f1 * tempIndex;
		f1 = AonMathUtils.round(f1 * daysTrim / diasActividad);
		return f1;
	}
	
//	public double calculateIngresoCuenta2021(int actNum, double daysAct, double daysTrim, double quota, double reductions, double tempIndex, double percent,double covid) {
//		if (isLastPeriod())
//			return 0.0;
//		double diasActividad = 0;
//		if (tempIndex == 0) {
//			tempIndex = 1;
//			diasActividad = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod421), FiscalUtils.getPeriodEnd(mod421)) + 1;
//		} else {
//			diasActividad = daysAct;
//		}
//		double q = 0.0;
//		double comisiones = 0.0;
//		for (int i = 0; i < 7; i++ ) {
//			double result = this.mod421.getActivityList().get(actNum).getModules().get(i).getResult();
//			if ( AonMathUtils.isZero(result)) {
//				double value = this.mod421.getActivityList().get(actNum).getModules().get(i).getValue();
//				double factor = this.mod421.getActivityList().get(actNum).getModules().get(i).getFactor();
//				result = AonMathUtils.round(value * factor);
//			};
//			String desc   = this.mod421.getActivityList().get(actNum).getModules().get(i).getDescription();
//			if ( AonStringUtils.isNotBlank(desc) && AonStringUtils.containsIgnoreCase(desc, "comisiones")) {
//				comisiones = result;
//			} else {
//				q = q + result; 
//			}
//		}
//		double f1 = ((q - reductions) * percent / 100) + comisiones;
//		f1 = f1 * tempIndex;
//		f1 = AonMathUtils.round(f1 * daysTrim / diasActividad);
//		return f1;
//	}

	public double calculateResultadoAnual(double quota, double supportedQuotas, double tempIndex) {
		
		if (!isLastPeriod()) 
			return 0.0;
		double q = AonMathUtils.round((quota - supportedQuotas));
		if (AonMathUtils.isNotZero(tempIndex)) {
			q = AonMathUtils.round(q * tempIndex);   
		}
		return q;
	}
	
	public double calculateCuotaMinima(double quota, double porQuotaMin, double tempIndex) {
		
		if (!isLastPeriod()) 
			return 0.0;
		
		double q = AonMathUtils.round( quota * porQuotaMin / 100 );
		if (AonMathUtils.isNotZero(tempIndex)) {
			q = AonMathUtils.round( q * tempIndex);   
		}
		return q;
	}
	
	// Comprobación de la casilla 78 (Cuotas a compensar de periodos anteriores aplicadas en este periodo)
//	public double checkC78(double c78, double c110, double c66, double c77) {
//		// No puede ser mayor que la casilla c110		
//		if (c78 > c110)
//			c78 = c110;
//		double c66_77 = AonMathUtils.round(c66+c77);
//		if (!isLastPeriod() && !isDevReg())  {
//			// No puede ser mayor que el resultado del modelo hasta ahora (casillas 66 y 77)
//			if (c78 > (c66_77))
//				c78 = (c66_77);
//		}
//		// No puede ser negativa
//		if (c78 < 0)
//			c78 = 0;	
//		return c78;
//		
//	}
	
	public double calculateResult(int activity,double value,double factor) {
		if (hasActivity(activity)) {
			BigDecimal i = new BigDecimal(Double.toString(value)).setScale(2, RoundingMode.HALF_UP);
			BigDecimal f = new BigDecimal(Double.toString(factor)).setScale(4, RoundingMode.HALF_UP);
			return AonMathUtils.round(i.multiply(f).doubleValue());
		} else {
			return 0.0;
		}
	}
	
//	public double calculateC111(double casilla69, double casilla70, double casilla71) {
//		
//        // CALCULO DE LA CASILLA 111 A PARTIR DE 09 O 3T DE 2024, SI RESULTADO NEGATIVO Y ES RECTIFICATIVA
//        // ESTO ME DICEN EN LA AEAT DESARROLLADORES:
//        // Respecto a la casilla 111 en el formulario de presentación del 421 esta en la pantalla de presentación como "Importe de la rectificación".
//        // Sólo tiene valor si es una autoliquidación rectificativa, la casilla 70 tiene contenido y la casilla 71 menor que cero
//        // Además es una casilla calculada de la siguiente forma:
//        // - Si (69) <= 0 y 70 <= -71 --> (111) = (70)
//        // - Si (69) <= 0 y 70 >  -71 --> (111) = (70) - (69) - (109) = -71
//        // - Si (69) > 0 --> (111) = (70) - (69) - (109) = -71 
//
//        double casilla111 = 0;
//        if (this.mod421.isComplementary() && casilla70 != 0 && casilla71 < 0)
//        {
//            // Si (69) <= 0 y 70 <= -71 --> (111) = (70)
//            // Si (69) <= 0 y 70 >  -71 --> (111) = (70) - (69) - (109)= -71
//            if (casilla69 <= 0)
//            {
//                if (casilla70 <= Math.abs(casilla71))
//                    casilla111 = casilla70;
//                else
//                    casilla111 = Math.abs(casilla71);
//            }
//            // Si (69) > 0 --> (111) = (70) - (69) - (109) = -71 
//            else
//            {
//                casilla111 = Math.abs(casilla71);
//            }
//        }
//        else
//        {
//            casilla111 = 0;
//        }
//        return casilla111;
//        
//    }
	
}

