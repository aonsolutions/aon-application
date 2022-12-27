package net.aonsolutions.aon.sii.aeat;

import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.RegistroSii.PeriodoLiquidacion;


public class SIIBuilt {

	/**
	 * Devuelve el periodo Impositivo ó periodo de liquidación.
	 * 
	 * @param invoice
	 * @return PeriodoImpositivo
	 */
	protected PeriodoLiquidacion periodoLiquidacion(VatContext vat, Boolean anual){
		Integer year = AonDateUtils.getYear(vat.getTaxDate());
		Integer month = AonDateUtils.getMonth(vat.getTaxDate()) + 1;
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoLiquidacion periodo = new PeriodoLiquidacion();
		periodo.setEjercicio(year.toString());
		if(anual){
			periodo.setPeriodo("0A");
		} else {
			periodo.setPeriodo(p);// mes (01,02,03,04,...,12) || anual (0A));
		}
		return periodo;
	}
	

	/**
	 * Devuelve el periodo Impositivo ó periodo de liquidación.
	 * 
	 * @param invoice
	 * @return PeriodoImpositivo
	 */
	protected PeriodoLiquidacion periodoLiquidacion(Date invDate, Date date, boolean anual, boolean errorPeriodo){
		Integer nowDay = errorPeriodo ? AonDateUtils.getDay(date) : AonDateUtils.getDay(new Date());
		Integer nowMonth = errorPeriodo ? AonDateUtils.getMonth(date) + 1 : AonDateUtils.getMonth(new Date()) + 1;
		Integer nowYear = errorPeriodo ? AonDateUtils.getYear(date) : AonDateUtils.getYear(new Date());
		
		Integer invYear = AonDateUtils.getYear(invDate);
		Integer invMonth = AonDateUtils.getMonth(invDate) + 1;
		
		Integer year = AonDateUtils.getYear(date);
		Integer month = AonDateUtils.getMonth(date) + 1;
		
		if(nowYear > invYear) nowMonth = nowMonth + 12;
		Integer diffMonth = nowMonth-invMonth; 
		if(!invMonth.equals(nowMonth) && diffMonth < 2 && nowDay <= 15) {
			year = invYear;
			month = invMonth;
		} 
		
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoLiquidacion periodo = new PeriodoLiquidacion();
		periodo.setEjercicio(year.toString());
		if(anual){
			periodo.setPeriodo("0A");
		} else {
			periodo.setPeriodo(p);
		}
		return periodo;
	}
}
