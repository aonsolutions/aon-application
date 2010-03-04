package com.code.aon.ui.payroll.wizard;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.ItStatus;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.Tipoit;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.principales.personas.Parteconf;
import com.code.aon.payroll.principales.personas.ParteconfId;
import com.code.aon.payroll.principales.personas.Parteit;
import com.code.aon.payroll.principales.personas.ParteitPK;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class ItWizardController extends BasicController{
	
	private List<ITransferObject> trabajadores;
	private IManagerBean trabajadorBean;
	private IManagerBean parteitBean;
	private IManagerBean parteconfBean;
	private ParteRen parteRen;
	private Boolean saved;
	private ItStatus trabajadorStatus;
	private List<SelectItem> itStatus;
	private List<SelectItem> tipoit;
	
	public ItStatus getTrabajadorStatus() {
		return trabajadorStatus;
	}

	public void setTrabajadorStatus(ItStatus trabajadorStatus) {
		this.trabajadorStatus = trabajadorStatus;
	}
	
	public Boolean getSaved() {
		if(saved==null){
			return false;
		}
		return saved;
	}

	public void setSaved(Boolean saved) {
		this.saved = saved;
	}

	public IManagerBean getParteconfBean() throws ManagerBeanException {
		if(parteconfBean==null){
			parteconfBean = BeanManager.getManagerBean(Parteconf.class);
		}
		return parteconfBean;
	}

	public ParteRen getParteRen() {
		return parteRen;
	}

	public void setParteRen(ParteRen parteRen) {
		this.parteRen = parteRen;
	}

	public IManagerBean getTrabajadorBean() throws ManagerBeanException {
		if(trabajadorBean==null){
			trabajadorBean = BeanManager.getManagerBean(Trabajador.class);
		}
		return trabajadorBean;
	}
	
	public IManagerBean getParteitBean() throws ManagerBeanException {
		if(parteitBean==null){
			parteitBean = BeanManager.getManagerBean(Parteit.class);
		}
		return parteitBean;
	}

	public List<ITransferObject> getTrabajadores() throws ManagerBeanException {
		if(trabajadores==null){
			initializeTrabajadores();
		}
		return trabajadores;
	}
	
	public void buildItStatus() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
		.getLocale();
		itStatus = new LinkedList<SelectItem>();
		
		if(getTrabajadorStatus().equals(ItStatus.ALTA)){
			String name = ItStatus.BAJA.getName(locale);
			SelectItem item = new SelectItem(ItStatus.BAJA, name);
			itStatus.add(item);
		} else if(getTrabajadorStatus().equals(ItStatus.BAJA)){
			String name = ItStatus.RENOVACION.getName(locale);
			SelectItem item = new SelectItem(ItStatus.RENOVACION, name);
			itStatus.add(item);
			name = ItStatus.ALTA.getName(locale);
			item = new SelectItem(ItStatus.ALTA, name);
			itStatus.add(item);
		}
		
	}

	public List<SelectItem> getListaItStatus() {
		try {
			searchStatus();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buildItStatus();
//		if (itStatus == null) {
//		}
		return itStatus;
	}
	
	public List<SelectItem> getListaTipoIt() {
		if (tipoit == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoit = new LinkedList<SelectItem>();
			for (Tipoit tit : Tipoit.values()) {
				String name = tit.getName(locale);
				SelectItem item = new SelectItem(tit, name);
				tipoit.add(item);
			}
		}
		return tipoit;
	}

	private void initializeTrabajadores() throws ManagerBeanException {
		Persona persona = (Persona)getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getTrabajadorBean().getFieldName(IPayrollAlias.TRABAJADOR_PERSONA_CDG), persona.getCdg());
		criteria.addNullExpression(getTrabajadorBean().getFieldName(IPayrollAlias.TRABAJADOR_FECBAJ));
		setTrabajadores(getTrabajadorBean().getList(criteria));
	}

	public void setTrabajadores(List<ITransferObject> list) {
		this.trabajadores = list;
	}

	private void reset() {
		setTrabajadores(null);
		initializeParteRen();
		setSaved(false);
	}

	private void initializeParteRen() {
//		Recuperar el estado actual del parte
		
		setParteRen(new ParteRen());
		getParteRen().setParteit(new Parteit());
		
		
	}
	
	@Override
	public void onReset(ActionEvent arg0) {
		super.onReset(arg0);
		reset();
		setSaved(false);
		setParteRen(null);
		setModel(null);
	}

	@Override
	public void onSelect(ActionEvent arg0) {
		super.onSelect(arg0);
		reset();
	}
	
	public void onRestore(ActionEvent arg0) {
		String msg = "Deshacer la accion";
		AonUtil.addErrorMessage(msg);
	}
	
	private void searchStatus() throws ManagerBeanException {
//		if(getTrabajadores().size()>0){
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG), ((Trabajador)getTrabajadores().get(0)).getCdg());
			criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), false);
//		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_BAJPROC), true);
			// se da por supuesto que solo hay un parte de baja, el primero y unico de la lista
//		Parteit pit = (Parteit)getParteitBean().getList(criteria).get(0);
			List<ITransferObject> pit = getParteitBean().getList(criteria);
			if(pit.size()<=0){
				trabajadorStatus = ItStatus.ALTA;
			} else {
				trabajadorStatus = ItStatus.BAJA;
			}
//		}
		
	}

	public void onSave(ActionEvent event) {
		// validaciones
		if(!Utils.validarMascara(getParteRen().getCias(), "##########A")){
			String msg = "Numero cias no valido. Debe cumplir con la mascara 9999999999X.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			// start transaction
			for(ITransferObject to: getTrabajadores()){
				Trabajador t = (Trabajador)to;
				if (getParteRen().getStatus() == ItStatus.BAJA) {
					insertBaja(t);
				} else if (getParteRen().getStatus() == ItStatus.ALTA) {
					insertAlta(t);
				} else {
					insertRenovacion(t);
				}
			}
			
			setSaved(true);
			// commit
		} catch (Throwable e) {
			// rollback
			e.printStackTrace();
		}
	}

	private void insertRenovacion(Trabajador t) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG), t.getCdg());
		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), false);
		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_BAJPROC), true);
		// se da por supuesto que solo hay un parte de baja, el primero y unico de la lista
		Parteit pit = (Parteit)getParteitBean().getList(criteria).get(0);
		
		ParteconfId id = new ParteconfId();
		Parteconf p = new Parteconf();
		id.setCdg(pit.getId().getCdg());
		id.setFecini(pit.getId().getFecini());
//		String numero = Utils.maxCode("Parteconf", "id.numero","cdg="+id.getCdg()+" and fecini="+id.getFecini() );
		String numero = Utils.maxCode("Parteconf", "id.numero","cdg="+id.getCdg());
		id.setNumero(Integer.parseInt(numero)+1); //Número parte confirmación ???????????????????????
		p.setId(id);
		p.setCias(getParteRen().getCias());
		p.setFecconf(getParteRen().getFecconf());
		p.setNumcol(getParteRen().getNumcol());
		p.setParproc(getParteRen().getParproc());
		p.setParproc("N");
		p.setParteit(pit);
		
		getParteconfBean().insert(p);
		
	}

	private void insertAlta(Trabajador t) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG), t.getCdg());
		criteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), false);
		for(ITransferObject to: getParteitBean().getList(criteria)){
			Parteit p = (Parteit)to;
			p.setAltproc(true);
			p.setProcesado(true);
			p.setCiasalt(getParteRen().getCias());
			p.setFecfin(getParteRen().getFecconf());
			p.setNumcolalt(getParteRen().getNumcol());
			getParteitBean().update(p);
		}
		
	}

	private void insertBaja(Trabajador t) throws ManagerBeanException {
		ParteitPK id = new ParteitPK();
		id.setCdg(t.getCdg());
		id.setFecini(getParteRen().getFecconf());
		Parteit pit = new Parteit();
		pit.setId(id);
		pit.setTipoit(getParteRen().getParteit().getTipoit());
		pit.setCiasbaj(getParteRen().getCias());
		pit.setNumcolbaj(getParteRen().getNumcol());
		pit.setBajproc(true);
		pit.setAltproc(false);
		pit.setProret(Prorateo.PROMENSUAL);
		pit.setEmprper(t);
		getParteitBean().insert(pit);
	}
	
	

}
