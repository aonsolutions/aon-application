package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
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

	public boolean isLastPeriod() {
		return this.mod303.getPeriod() == Period.T4 || this.mod303.getPeriod() == Period.M12;
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
	
	public double calculateIndiceTemporada(Double dias) {
		if (dias != null) {
			if (dias > 0 && dias <= 60) return 1.50;
			if (dias > 60 && dias <= 120) return 1.35;
			if (dias > 120) return 1.25;
		}
		return 0;
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
}
