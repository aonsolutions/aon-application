package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.payroll.resultados.irpf.Impresos11x;
import com.code.aon.payroll.resultados.irpf.Impresos190;

public class Impresos190Controller extends PayrollBasicController {

	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());

	
	/**
	 * Genera un numero autonumerico para el codigo de la paga extra
	 * @param event
	 */
	public void generarNumero(ActionEvent event) {

		String num = Utils.maxCode("Impresos190", "cdg");
		((Impresos190) getTo()).setCdg(Integer.parseInt(num) + 1);
	}
	
	/**
	 * Establece valores por defecto a los campos nulos
	 */
	public void setDefaultFields() {
		
		Impresos190 to =(Impresos190) getTo();
		Calendar calendar = Calendar.getInstance();
		
		if(to.getNumPercep()==null)
			to.setNumPercep(0);
		if(to.getImpPercep()==null)
			to.setImpPercep(new BigDecimal(0));
		if(to.getImpRetenc()==null)
			to.setImpRetenc(new BigDecimal(0));
		if(to.getFecha()==null)
			to.setFecha(calendar.getTime());
		if(to.getAnio()==null)
			to.setAnio(calendar.get(Calendar.YEAR));
		if(to.getEmprnif1()==null)
			to.setEmprnif1(to.getEmprnif());
		if(to.getEmprnif2()==null)
			to.setEmprnif2(to.getEmprnif());
		if (to.getAdmon()==null || to.getAdmon().getCdg()=="" || StringUtils.isEmpty(to.getAdmon().getCdg()))
			to.setAdmon(to.getEmprnif().getAdmon());
		if (to.getProvincia()==null || to.getProvincia().getCdg()=="" || StringUtils.isEmpty(to.getProvincia().getCdg()))
			setDefaultProvincia(null);
		
	}
	
	/**
	 * Establece el valor por defecto de la provincia 
	 */
	public void setDefaultProvincia(ActionEvent event) {
		System.out.println("setDefaultProvincia");
		
		String cdg = ((Impresos190) getTo()).getEmprnif().getAdmon().getCdg().substring(0, 2);
		((Impresos190) getTo()).getProvincia().setCdg(cdg);
	}


}
