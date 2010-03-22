package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.resultados.salarios.Nominaex;
import com.code.aon.ql.Criteria;

public class NominaexController extends PayrollBasicController {

	/*
	 * Implementacion de impresion
	 * Implementacion de envio por email
	 */
	private BigDecimal total;
	private BigDecimal baseIrpf;
	private List<SelectItem> fijoVar;

	/**
	 * Recupera los tipos de retribuciones
	 * 
	 * @return
	 */
	public List<SelectItem> getListaFijovar() {
		if (fijoVar == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fijoVar = new LinkedList<SelectItem>();
			for (FijoVariable p : FijoVariable.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				fijoVar.add(item);
			}
		}
		return fijoVar;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getBaseIrpf() {
		return baseIrpf;
	}

	public void setBaseIrpf(BigDecimal baseIrpf) {
		this.baseIrpf = baseIrpf;
	}
	
	
	
	public void generarNumero(ActionEvent event){
		
		((Nominaex)getTo()).getId().setCdg(((Nominaex)getTo()).getEmprper().getCdg());
		String num = Utils.maxCode("Nominaex", "id.numero", "id.cdg="+((Nominaex)getTo()).getId().getCdg());
		((Nominaex)getTo()).getId().setNumero(Integer.parseInt(num)+1);
	}
	
	

	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		super.onEditSearch(arg0);
		
		emprper= new Trabajador();
	}

	/**
	 * Se incluyen manualmente a las búsquedas los campos lookup y de fechas 
	 */
	@Override
	public void onSearch(ActionEvent event) {

		try {
			//Búsqueda por campos LookUp
			if (emprper!=null && (emprper.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_EMPRPER_CDG), emprper.getCdg());
			}
			//Búsqueda por campos Date
			if (fecini != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECINI), fecini);
			}
			if (fecfin != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECFIN), fecfin);
			}
			if (feccob != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECCOB), feccob);
			}
			if (feccobreal != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECCOBREAL), feccobreal);
			}
			if (fecemi != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECEMI), fecemi);
			}
			if (fecant != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.NOMINAEX_FECANT), fecant);
			}
		} catch (ManagerBeanException e) {
			
			e.printStackTrace();
		}

		super.onSearch(event);
	}

	
	private Date fecini;
	private Date fecfin;
	private Date feccob;
	private Date feccobreal;
	private Date fecemi;
	private Date fecant;
	private Trabajador emprper;
	
	public Date getFecini() {
		return fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	public Date getFecfin() {
		return fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	public Date getFeccob() {
		return feccob;
	}

	public void setFeccob(Date feccob) {
		this.feccob = feccob;
	}

	public Date getFeccobreal() {
		return feccobreal;
	}

	public void setFeccobreal(Date feccobreal) {
		this.feccobreal = feccobreal;
	}

	public Date getFecemi() {
		return fecemi;
	}

	public void setFecemi(Date fecemi) {
		this.fecemi = fecemi;
	}

	public Date getFecant() {
		return fecant;
	}

	public void setFecant(Date fecant) {
		this.fecant = fecant;
	}

	public Trabajador getEmprper() {
		return emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}
	
	
	
	/**
	 * Comprueba los campos nulos
	 */
	public void verifyNullFields(){
		Nominaex to = ((Nominaex)getTo());
		
		if(to.getImporte() == null)
			to.setImporte(new BigDecimal(0));
		if(to.getFijovar() == null)
			to.setFijovar(FijoVariable.FIJO);
		if(to.getIrpf() == null)
			to.setIrpf(new BigDecimal(0));
		if(to.getImpirpf() == null)
			to.setImpirpf(new BigDecimal(0));
		
		to.setDivisa(to.getEmprper().getEmpresa().getDivisa());
		if(to.getDivisa() == null){
			Criteria criteria = new Criteria();
			try {
				criteria.addEqualExpression(IPayrollAlias.DIVISA_CDG, "2");
				List<ITransferObject> listaDivisa = BeanManager.getManagerBean(Divisa.class).getList(criteria);
				Divisa divisa = (Divisa)(listaDivisa.iterator().next());
			} catch (ManagerBeanException e) {
				// LOGGER
				e.printStackTrace();
			}
		}
		
		if(to.getId().getCdg() == null)
			to.getId().setCdg(to.getEmprper().getCdg());
		if(to.getComplemento().getCdg() == "" || to.getComplemento().getCdg() == null)
			to.setComplemento(null);
		
	}

}
