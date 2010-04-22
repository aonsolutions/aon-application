package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import javax.faces.event.ActionEvent;

import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.resultados.salarios.Finiquito;

public class FiniquitoController extends PayrollBasicController {

	
	//private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());
	private Trabajador emprper;
	
	public Trabajador getEmprper() {
		return emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}
	
	/**
	 * Genera un numero autonumerico para el codigo 
	 * @param event
	 */
	public void generarNumero(ActionEvent event) {

		String num = Utils.maxCode("Finiquito", "cdg");
		((Finiquito) getTo()).setCdg(Integer.parseInt(num) + 1);
	}
	
	/**
	 * Establece valores por defecto a los campos nulos
	 */
	public void setDefaultFields() {
		
		
		Finiquito to =(Finiquito) getTo();
		
		if(to.getImportesin()==null)
			to.setImportesin(new BigDecimal(0));
		if(to.getVacimporte()==null)
			to.setVacimporte(new BigDecimal(0));
		if(to.getTotalConceptos()==null)
			to.setTotalConceptos(new BigDecimal(0));
		if(to.getCostessemp()==null)
			to.setCostessemp(new BigDecimal(0));
		if (to.getDiasvac() == null)
			to.setDiasvac(0);
		if (to.getBase() == null)
			to.setBase(new BigDecimal(0));
		if (to.getBasecg() == null)
			to.setBasecg(new BigDecimal(0));
		if (to.getBaseacc() == null)
			to.setBaseacc(new BigDecimal(0));
		if (to.getIrpf() == null)
			to.setIrpf(new BigDecimal(0));
		if (to.getPrccg() == null)
			to.setPrccg(new BigDecimal(0));
		if (to.getPrcacc() == null)
			to.setPrcacc(new BigDecimal(0));
		if (to.getImporteIrpf() == null)
			to.setImporteIrpf(new BigDecimal(0));
		if (to.getImportecg() == null)
			to.setImportecg(new BigDecimal(0));
		if (to.getImporteacc() == null)
			to.setImporteacc(new BigDecimal(0));
		if (to.getLiquido()== null)
			to.setLiquido(new BigDecimal(0));
		if (to.getSimula()== null)
			to.setSimula("C");
		if (to.getDivisa()== null){
			to.setDivisa(new Divisa());
			to.getDivisa().setCdg("2");
		}

	}
	
	
	
	/*
	 * private Empresa emprnif; private Admon admon; private Provincia
	 * provincia;
	 */
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		super.onEditSearch(arg0);
		
		/*
		emprnif = new Empresa();
		admon = new Admon();
		provincia = new Provincia();
		*/
	}

	/**
	 * Se incluyen manualmente a las búsquedas los campos lookup y de fechas 
	 */
	@Override
	public void onSearch(ActionEvent event) {

		/*
		try {
			//Búsqueda por campos LookUp
			if (emprnif!=null && (emprnif.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS190_EMPRNIF_CDG), emprnif.getCdg());
			}
			if (admon!=null && StringUtils.isNotEmpty(admon.getCdg())) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS190_ADMON_CDG), admon.getCdg());
			}
			if (provincia!=null && StringUtils.isNotEmpty(provincia.getCdg())) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS190_PROVINCIA_CDG), provincia.getCdg());
			}
			
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			e.printStackTrace();
		}
		*/
		
		super.onSearch(event);
	}

	
}
