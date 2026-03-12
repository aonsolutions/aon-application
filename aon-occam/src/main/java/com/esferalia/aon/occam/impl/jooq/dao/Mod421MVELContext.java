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
	
	public boolean isToCompensate() {
		return this.mod421.isToCompensate();
	}
	
	public boolean isToDeposit() {
		return this.mod421.isToDeposit();
	}
	
	public boolean isToPayback() {
		return this.mod421.isToPayback();
	}

	public boolean hasActivity(int idx) {
		return 
			this.mod421.getActivityList() != null 
			&& idx < this.mod421.getActivityList().size()
			&& this.mod421.getActivityList().get(idx) != null
			&& AonStringUtils.isNotBlank( this.mod421.getActivityList().get(idx).getEpigraph() )
			;
	}
	
	public double calculateIndiceTemporada(int actIdx, Double dias) {
		if (dias != null) {
			if (this.mod421.getActivityList().get(actIdx).isAgraria() && !isLastPeriod())
				return 0.0; // Actividades agrarias: No se tiene en cuenta el índice corrector de temporada, en los trimestres 1T, 2T y 3T
			if (dias > 0 && dias <= 60) 
				return 1.50;
			if (dias > 60 && dias <= 120) 
				return 1.35;
			if (dias > 120) 
				return 1.25;
		}
		return 0.0;
	}
	
	public double calculatePorcentajeIngresoCuenta(int actIdx) {
		if (isLastPeriod()) 
			return 0.0;
		String epigraph = this.mod421.getActivityList().get(actIdx).getEpigraph();
		int specialEpigraph = this.mod421.getActivityList().get(actIdx).getSpecialEpigraph();
		ModulesCanarias2026.EpigraphCanarias epi = ModulesCanarias2026.EpigraphCanarias.getEpigraph(epigraph, specialEpigraph);
		double por = 0.0;
		if (epi != null) 
			por = epi.getPorcIng();
		return por;
	}

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
		for (int i = 0; i < 7; i++ ) {
			double result = this.mod421.getActivityList().get(actNum).getModules().get(i).getResult();
			if (AonMathUtils.isZero(result)) {
				double value = this.mod421.getActivityList().get(actNum).getModules().get(i).getValue();
				double factor = this.mod421.getActivityList().get(actNum).getModules().get(i).getFactor();
				result = AonMathUtils.round(value * factor);
			}
			q = q + result; 
		}
		double f1 = ((q) * percent / 100);
		// ACTIVIDADES AGRARIAS NO SE TIENE EN CUENTA DIAS DE ACTIVIDAD EN EL TRIMESTRE, NI ACTIVIDADES DE TEMPORADA
		if (!this.mod421.getActivityList().get(actNum).isAgraria()) {
			f1 = f1 * tempIndex; // Actividades de temporada: Ajuste por índice corrector de temporada
			f1 = AonMathUtils.round(f1 * daysTrim / diasActividad); // Actividades con trimestre incompleto: Ajuste por días de actividad en el trimestre
		}
		return f1;
	}
	
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
			q = AonMathUtils.round(q * tempIndex);
		}
		return q;
		
	}
	
	public double calculateResult(int activity,double value,double factor) {
		if (hasActivity(activity)) {
			BigDecimal i = new BigDecimal(Double.toString(value)).setScale(2, RoundingMode.HALF_UP);
			BigDecimal f = new BigDecimal(Double.toString(factor)).setScale(4, RoundingMode.HALF_UP);
			return AonMathUtils.round(i.multiply(f).doubleValue());
		} else {
			return 0.0;
		}
	}
	
}
