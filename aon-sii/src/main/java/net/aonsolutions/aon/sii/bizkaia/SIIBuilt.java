package net.aonsolutions.aon.sii.bizkaia;

import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.watson.server.AonDateUtils;

import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSii.PeriodoLiquidacion;


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
}
