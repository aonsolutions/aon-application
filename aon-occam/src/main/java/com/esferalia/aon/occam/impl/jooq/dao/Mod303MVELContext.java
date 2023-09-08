package com.esferalia.aon.occam.impl.jooq.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303MVELContext extends ModelMVELContext implements Map<String, Object> {
	
	private Mod303 mod303;
	
	public Mod303MVELContext(Mod303 mod303) {
		this.mod303 = mod303;
	}
	public Mod303 getMod303() {
		return mod303;
	}
	public boolean isLastPeriod() {
		return this.mod303.getPeriod() == Period.T4 || this.mod303.getPeriod() == Period.M12;
	}
	public boolean isDevReg() {
		return this.mod303.isDevReg();
	}
	public boolean isToCompensate() {
		return this.mod303.isToCompensate();
	}
	public boolean isToDeposit() {
		return this.mod303.isToDeposit();
	}
	public boolean isToPayback() {
		return this.mod303.isToPayback();
	}

	public boolean hasSimplifiedRegime() {
		Double a02 = (Double) get(Mod303Key.CT_A02.toString());
		return a02 != 2;
	}
	
	public boolean hasFarmerActivity(int idx) {
		return hasSimplifiedRegime() 
			&& this.mod303.getActivityFarmerList() != null 
			&& idx < this.mod303.getActivityFarmerList().size()
			&& this.mod303.getActivityFarmerList().get(idx) != null
			&& AonStringUtils.isNotBlank( this.mod303.getActivityFarmerList().get(idx).getCode() )
			;
	}
	
	public boolean hasActivity(int idx) {
		return hasSimplifiedRegime() 
			&& this.mod303.getActivityList() != null 
			&& idx < this.mod303.getActivityList().size()
			&& this.mod303.getActivityList().get(idx) != null
			&& AonStringUtils.isNotBlank( this.mod303.getActivityList().get(idx).getEpigraph() )
			;
	}
	
	public double calculateReduccion(int epigraph,double cuota,double lorca,double covid) {
		double red = 0.0;
		if ( AonMathUtils.equals(1, lorca)) {
			red = AonMathUtils.round(red + (cuota * 20 / 100));
		}
		if ( AonMathUtils.equals(1, covid)) {
			String epi = this.mod303.getActivityList().get(epigraph).getEpigraph();
			double percent =  
				("653.2".equals(epi) || "653.4".equals(epi) ||
				"653.5".equals(epi)  || "654.2".equals(epi) ||
				"654.5".equals(epi)  || "654.6".equals(epi) ||
				"659.3".equals(epi)  || "663.1".equals(epi) ||
				"671.4".equals(epi)  || "671.5".equals(epi) ||
				"672.1".equals(epi)  || "672.2".equals(epi) ||
				"672.3".equals(epi)  || "673.1".equals(epi) ||
				"673.2".equals(epi)  || "675".equals(epi) ||
				"676".equals(epi) 	 || "681".equals(epi) ||
				"682".equals(epi) 	 || "683".equals(epi) ||
				"721.1".equals(epi)  || "721.3".equals(epi))
			? 35.0
			: 20.0;
			red = AonMathUtils.round(red + (cuota * percent / 100));		
		}
		return red;
	}

	public double calculateReduccion2021(int epigraph,double cuota,double lorca,double covid) {
		double red = 0.0;
		if ( AonMathUtils.equals(1, lorca)) {
			red = AonMathUtils.round(red + (cuota * 20 / 100));
		}
		return red;
	}

	public double calculateIndiceTemporada(Double dias) {
		if (dias != null) {
			if (dias > 0 && dias <= 60) return 1.50;
			if (dias > 60 && dias <= 120) return 1.35;
			if (dias > 120) return 1.25;
		}
		return 0;
	}
	
	public double calculatePorcentajeIngresoCuenta2023(int actIdx,double covid) {
		if (isLastPeriod()) return 0.0;
		String epi = this.mod303.getActivityList().get(actIdx).getEpigraph();
		Epigraph epig = Modules2018.Epigraph.getEpigraph(epi);		
		double por = 0.0;
		if (epig != null) por = epig.getVatPorc();
		return por;
	}

	public double calculatePorcentajeIngresoCuenta2021(int actIdx,double covid) {
		if (isLastPeriod())
			return 0.0;
//		double por = this.mod303.getActivityList().get(actIdx).getPor();
		String epi = this.mod303.getActivityList().get(actIdx).getEpigraph();
		Epigraph epig = Modules2018.Epigraph.getEpigraph(epi);		
		double por = 0.0;
		if (epig != null)
			por = epig.getVatPorc();
		
        // Reducción COVID (20% o 35% según epigrafe)
		if (AonMathUtils.equals(1, covid)) {
//			String epi = this.mod303.getActivityList().get(actIdx).getEpigraph();
//			Epigraph epig = Modules2018.Epigraph.getEpigraph(epi);
//			if (AonNumberUtils.equals(epig.getVatPorc(), por)) {
				double percent = 100;  
				if ("419.1".equals(epi) || "419.2".equals(epi) || "419.3".equals(epi) || 
					"423.9".equals(epi) || "641".equals(epi)   || "642.1".equals(epi) || 
					"642.2".equals(epi) || "642.3".equals(epi) || "642.4".equals(epi) || 
					"642.5".equals(epi) || "642.6".equals(epi) || "643.1".equals(epi) || 
					"643.2".equals(epi) || "644.1".equals(epi) || "644.2".equals(epi) || 
					"644.3".equals(epi) || "644.6".equals(epi) || "647.1".equals(epi) || 
					"647.2".equals(epi) || "647.3".equals(epi) || "659.4".equals(epi) || 
					"691.1".equals(epi) || "691.2".equals(epi) || "691.9".equals(epi) || 
					"691.9".equals(epi) || "692".equals(epi)   || "699".equals(epi)   || 
					"721.2".equals(epi) || "722".equals(epi)   || "751.5".equals(epi) || 
					"757".equals(epi)   || "849.5".equals(epi) || "933.1".equals(epi) || 
					"933.9".equals(epi) || "967.2".equals(epi) || "971.1".equals(epi) || 
					"972.1".equals(epi) || "972.2".equals(epi) || "973.3".equals(epi))  {
					percent = 20;		
				} else if (
					"653.2".equals(epi)  || "653.4".equals(epi) ||
					"653.5".equals(epi)  || "654.2".equals(epi) ||
					"654.5".equals(epi)  || "654.6".equals(epi) ||
					"659.3".equals(epi)  || "663.1".equals(epi) ||
					"671.4".equals(epi)  || "671.5".equals(epi) ||
					"672.1".equals(epi)  || "672.2".equals(epi) ||
					"672.3".equals(epi)  || "673.1".equals(epi) ||
					"673.2".equals(epi)  || "675".equals(epi)   ||
					"676".equals(epi) 	 || "681".equals(epi)   ||
					"682".equals(epi) 	 || "683".equals(epi)   ||
					"721.1".equals(epi)  || "721.3".equals(epi)) {
					percent = 35;
				}
				por = AonMathUtils.round(por - (por * percent / 100));		
//			}
		}
		return por;
	}

	public double calculateIngresoCuenta(int actNum, double daysAct, double daysTrim, double quota, double reductions, double tempIndex, double percent) {
		if (isLastPeriod())
			return 0.0;
		double diasActividad = 0;
		if (tempIndex == 0) {
			tempIndex = 1;
			diasActividad = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod303), FiscalUtils.getPeriodEnd(mod303)) + 1;
		} else {
			diasActividad = daysAct;
		}

		double f1 = (quota - reductions) * percent / 100;
		f1 = f1 * tempIndex;
		f1 = AonMathUtils.round(f1 * daysTrim / diasActividad);
		return f1;
	}
	
	public double calculateIngresoCuenta2021(int actNum, double daysAct, double daysTrim, double quota, double reductions, double tempIndex, double percent,double covid) {
		if (isLastPeriod())
			return 0.0;
		double diasActividad = 0;
		if (tempIndex == 0) {
			tempIndex = 1;
			diasActividad = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod303), FiscalUtils.getPeriodEnd(mod303)) + 1;
		} else {
			diasActividad = daysAct;
		}
		double q = 0.0;
		double comisiones = 0.0;
		for (int i = 0; i < 7; i++ ) {
			double result = this.mod303.getActivityList().get(actNum).getModules().get(i).getResult();
			if ( AonMathUtils.isZero(result)) {
				double value = this.mod303.getActivityList().get(actNum).getModules().get(i).getValue();
				double factor = this.mod303.getActivityList().get(actNum).getModules().get(i).getFactor();
				result = AonMathUtils.round(value * factor);
			};
			String desc   = this.mod303.getActivityList().get(actNum).getModules().get(i).getDescription();
			if ( AonStringUtils.isNotBlank(desc) && AonStringUtils.containsIgnoreCase(desc, "comisiones")) {
				comisiones = result;
			} else {
				q = q + result; 
			}
		}
		double f1 = ((q - reductions) * percent / 100) + comisiones;
		f1 = f1 * tempIndex;
		f1 = AonMathUtils.round(f1 * daysTrim / diasActividad);
		return f1;
	}

	public double calculateResultadoAnual(double quota, double reductions, double supportedQuotas, double tempIndex) {
		
		if (!isLastPeriod()) return 0.0;
		double q = AonMathUtils.round((quota - reductions - supportedQuotas));
		if (AonMathUtils.isNotZero(tempIndex)) {
			q = AonMathUtils.round( q * tempIndex);   
		}
		return q;
	}
	
	public double calculateCuotaMinima(double quota, double reductions, double porQuotaMin, double devQuota,double tempIndex) {
		
		if (!isLastPeriod()) return 0.0;
		
		double q = AonMathUtils.round( ( (quota - reductions ) * porQuotaMin / 100) + devQuota );
		if (AonMathUtils.isNotZero(tempIndex)) {
			q = AonMathUtils.round( q * tempIndex);   
		}
		return q;
	}
	
	// Comprobación de la casilla 78 (Cuotas a compensar de periodos anteriores aplicadas en este periodo)
	public double checkC78(double c78, double c110, double c66, double c77) {
		// No puede ser mayor que la casilla c110		
		if (c78 > c110)
			c78 = c110;
		double c66_77 = AonMathUtils.round(c66+c77);
		if (!isLastPeriod() && !isDevReg())  {
			// No puede ser mayor que el resultado del modelo hasta ahora (casillas 66 y 77)
			if (c78 > (c66_77))
				c78 = (c66_77);
		}
		// No puede ser negativa
		if (c78 < 0)
			c78 = 0;	
		return c78;
		
	}
	public double calculateResult(int activity,double value,double factor) {
		if (hasActivity(activity)) {
			BigDecimal i = new BigDecimal(Double.toString(value)).setScale(2, RoundingMode.HALF_UP);			
			BigDecimal f = new BigDecimal(Double.toString(factor)).setScale(2, RoundingMode.HALF_UP);
			return AonMathUtils.round(i.multiply(f).doubleValue());
		} else {
			return 0.0;
		}
	}
	
}

