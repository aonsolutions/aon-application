package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;
import com.esferalia.aon.payroll.core.it.IParteConfirmacionIT;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;

public class RemesableIT implements Serializable {
	
	private static final long serialVersionUID = 422693852102440685L;
	
	private TipoOperacionIT operacion;
	private boolean selected;
	private IParteIT parteIT;
	private IParteConfirmacionIT confirmacionIT;
	private IParteITDAO parteITDAO;
	private IRemesaParteIT remesaParteIT;

	public TipoOperacionIT getOperacion() {
		return operacion;
	}
	public void setOperacion(TipoOperacionIT operacion) {
		this.operacion = operacion;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public IParteIT getParteIT() {
		return parteIT;
	}
	public void setParteIT(IParteIT parteIT) {
		this.parteIT = parteIT;
	}
	
	public IParteConfirmacionIT getConfirmacionIT() {
		return confirmacionIT;
	}
	public void setConfirmacionIT(IParteConfirmacionIT confirmacionIT) {
		this.confirmacionIT = confirmacionIT;
	}
	
	public boolean isBaja() {
//		return (getParteIT().getFechaAlta() == null);
		return getOperacion() == TipoOperacionIT.BAJA;
	}
	public boolean isAlta() {
//		return (getParteIT().getFechaAlta() != null && getConfirmacionIT() == null);
		return getOperacion() == TipoOperacionIT.ALTA;
	}
	public boolean isConfirmacion() {
//		return (getParteIT().getFechaAlta() != null && getConfirmacionIT() != null);
		return getOperacion() == TipoOperacionIT.CONFIRMACION;
	}
	
	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}
	public void setRemesaParteIT(IRemesaParteIT remesaParteIT) {
		this.remesaParteIT = remesaParteIT;
	}
	private IRemesaParteIT getRemesaParteIT() throws PayrollException{
		return remesaParteIT;
	}
	
	public IRemesaParteIT getNewRemesaParteIT() throws PayrollException {
		IParteIT p = (getOperacion() == TipoOperacionIT.CONFIRMACION)?getConfirmacionIT().getParteIT():getParteIT(); 
		setRemesaParteIT(getParteITDAO().initializePartesRemesa());
		getRemesaParteIT().setEmpleado(p.getEmpleado());
		getRemesaParteIT().setFechaBaja(p.getFechaBaja());
		getRemesaParteIT().setTipoOperacionIT(getOperacion());
		getRemesaParteIT().setBaseRetribucionPeriodoAnterior(p.getBaseRetribucionPeriodoAnterior());
		getRemesaParteIT().setDiasPeriodoAnterior(p.getDiasPeriodoAnterior());
		getRemesaParteIT().setBaseReguladoraDiaria(p.getBaseReguladoraDiaria());
		getRemesaParteIT().setBaseDiariaContingenciasComunes(p.getBaseDiariaContingenciasComunes());
		getRemesaParteIT().setBaseDiariaAccidentesTrabajo(p.getBaseDiariaAccidentesTrabajo());
		getRemesaParteIT().setPrestacionDiaria60(p.getPrestacionDiaria60());
		getRemesaParteIT().setPrestacionDiaria75(p.getPrestacionDiaria75());
		if(getOperacion() == TipoOperacionIT.CONFIRMACION){
			buildRemesaParteITConfirmacion();
		}else{ 
			if(getOperacion() == TipoOperacionIT.ALTA){
				buildRemesaParteITAlta();
			}else if(getOperacion() == TipoOperacionIT.BAJA){
				buildRemesaParteITBaja();
			}
			getRemesaParteIT().setTipoContingencia(p.getTipoContingencia());
			getRemesaParteIT().setRecaida(p.isRecaida());
			getRemesaParteIT().setProrrateoCotizacion(p.getProrrateoCotizacion());
		}
		return getRemesaParteIT();
	}
	
	private void buildRemesaParteITAlta() throws PayrollException {
		getRemesaParteIT().setFechaParte(getParteIT().getFechaAlta());
		getRemesaParteIT().setNumeroColegiado(getParteIT().getNumeroColegiadoAlta());
		getRemesaParteIT().setCias(getParteIT().getCiasAlta());
	}
	
	private void buildRemesaParteITBaja() throws PayrollException {
		getRemesaParteIT().setFechaParte(getParteIT().getFechaBaja());
		getRemesaParteIT().setNumeroColegiado(getParteIT().getNumeroColegiadoBaja());
		getRemesaParteIT().setCias(getParteIT().getCiasBaja());
	}
	
	private void buildRemesaParteITConfirmacion() throws PayrollException {
		getRemesaParteIT().setFechaParte(getConfirmacionIT().getFecha());
		getRemesaParteIT().setNumeroColegiado(getConfirmacionIT().getNumeroColegiado());
		getRemesaParteIT().setCias(getConfirmacionIT().getCias());
		getRemesaParteIT().setNumero(getConfirmacionIT().getNumero());
		getRemesaParteIT().setTipoContingencia(getConfirmacionIT().getParteIT().getTipoContingencia());
		getRemesaParteIT().setRecaida(getConfirmacionIT().getParteIT().isRecaida());
		getRemesaParteIT().setProrrateoCotizacion(getConfirmacionIT().getParteIT().getProrrateoCotizacion());
	}
	
	public void setParteProcesado() throws PayrollException {
		if(getOperacion() == TipoOperacionIT.CONFIRMACION){
			getConfirmacionIT().setProcesado(true);
			getParteITDAO().accept(getConfirmacionIT());
		}else if(getOperacion() == TipoOperacionIT.ALTA){
			getParteIT().setBajaProcesada(true);
			getParteITDAO().accept(getParteIT());
		}else if(getOperacion() == TipoOperacionIT.BAJA){
			getParteIT().setAltaProcesada(true);
			getParteITDAO().accept(getParteIT());
		}
	}
	
}
