package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.payroll.resultados.irpf.LinImpresos190;
import com.code.aon.ui.form.LinesController;

public class Linimpresos190Controller extends LinesController {

	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());
	
	
	/**
	 * Establece valores por defecto a los campos nulos
	 */
	public void setDefaultFields() {
		
		LinImpresos190 to =(LinImpresos190) getTo();
		
		if (to.getImpPerDin()==null)
			to.setImpPerDin(new BigDecimal(0));
		if (to.getImpRetDin()==null)
			to.setImpRetDin(new BigDecimal(0));
		if (to.getImpPerEsp()==null)
			to.setImpPerEsp(new BigDecimal(0));
		if (to.getImpIngCta()==null)
			to.setImpIngCta(new BigDecimal(0));
		if (to.getImpIngRep()==null)
			to.setImpIngRep(new BigDecimal(0));
		if (to.getImpReducc()==null)
			to.setImpReducc(new BigDecimal(0));
		if (to.getImpGastos()==null)
			to.setImpGastos(new BigDecimal(0));
		if (to.getImpPension()==null)
			to.setImpPension(new BigDecimal(0));
		if (to.getImpAnual()==null)
			to.setImpAnual(new BigDecimal(0));
		
		
		generarNumero(null);
	}
	
	/**
	 * Genera un numero autonumerico para el codigo de la paga extra
	 * @param event
	 */
	public void generarNumero(ActionEvent event) {
		Integer cdg = ((LinImpresos190) getTo()).getId().getCdg(); 
		String num = Utils.maxCode("LinImpresos190", "id.linea","id.cdg="+cdg.toString());
		((LinImpresos190) getTo()).getId().setLinea(Integer.parseInt(num) + 1);
	}

}
