package com.code.aon.ui.payroll.wizard;


import java.util.Calendar;
import java.util.GregorianCalendar;
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
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
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
	
//	private final static Logger LOGGER = LoggerFactory.getLogger(SummaryProvider.class);

	private List<ITransferObject> contratos;
	private IManagerBean trabajadorBean;
	private IManagerBean parteitBean;
	private IManagerBean parteconfBean;
	private ParteRen parteRen;
	private Boolean finished;
	private Boolean recaida;
	private ItStatus trabajadorStatus;
	private List<SelectItem> itStatus;
	private List<SelectItem> tipoit;
	private String numParteRenovacion;
	
	public String getNumParteRenovacion() {
		return numParteRenovacion;
	}

	public void setNumParteRenovacion(String numParteRenovacion) {
		this.numParteRenovacion = numParteRenovacion;
	}

	public ItStatus getTrabajadorStatus() {
		return trabajadorStatus;
	}

	public void setTrabajadorStatus(ItStatus trabajadorStatus) {
		this.trabajadorStatus = trabajadorStatus;
	}
	
	public Boolean getRenovacionStatus(){
		if(getTrabajadorStatus()==null){
			return false;
		}
		return getTrabajadorStatus().equals(ItStatus.RENOVACION);
	}
	
	public Boolean getFinished() {
		if(finished==null){
			return false;
		}
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Boolean getRecaida() {
		if(recaida==null){
			return false;
		}
		return recaida;
	}
	
	public void setRecaida(Boolean recaida) {
		this.recaida = recaida;
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

	public List<ITransferObject> getContratos() throws ManagerBeanException {
		if(contratos==null){
			initializeContratos();
		}
		return contratos;
	}
	
	public void setContratos(List<ITransferObject> list) {
		this.contratos = list;
	}
	
	private void initializeContratos() throws ManagerBeanException {
		Persona persona = ((Trabajador)getTo()).getPersona();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getTrabajadorBean().getFieldName(IPayrollAlias.TRABAJADOR_PERSONA_CDG), persona.getCdg());
		criteria.addNullExpression(getTrabajadorBean().getFieldName(IPayrollAlias.TRABAJADOR_FECBAJ));
		setContratos(getTrabajadorBean().getList(criteria));
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
			searchSuggestData();
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

	private void reset() {
		setContratos(null);
		initializeParteRen();
		setFinished(false);
		setRecaida(false);
		setNumParteRenovacion(null);
	}

	private void initializeParteRen() {
//		Recuperar el estado actual del parte
		
		setParteRen(new ParteRen());
		getParteRen().setParteit(new Parteit());
		
		
	}
	
	private void searchSuggestData() throws ManagerBeanException {
		Criteria allParteitCriteria = new Criteria();
		allParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG), ((Trabajador)getTo()).getCdg());
		List<ITransferObject> pit = getParteitBean().getList(allParteitCriteria);
		if(pit.size()>0){
			String ciasalt = ((Parteit)pit.get(pit.size()-1)).getCiasalt();
			String numcolalt = ((Parteit)pit.get(pit.size()-1)).getNumcolalt();
			String ciasbaj = ((Parteit)pit.get(pit.size()-1)).getCiasbaj();
			String numcolbaj = ((Parteit)pit.get(pit.size()-1)).getNumcolbaj();
//			recoge los datos del ultimo parte
			getParteRen().setCias(ciasalt);
			getParteRen().setNumcol(numcolalt);
			
//			se busca el estado actual del trabajador
			Criteria bajasParteitCriteria = new Criteria();
			bajasParteitCriteria.addExpression(allParteitCriteria.getExpression());
			bajasParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), false);
			// se da por supuesto que solo hay un parte de baja, el primero y unico de la lista
			pit = getParteitBean().getList(bajasParteitCriteria);
			if(pit.size()<=0){
				trabajadorStatus = ItStatus.ALTA;
			} else {
				trabajadorStatus = ItStatus.BAJA;
				getParteRen().setCias(ciasbaj);
				getParteRen().setNumcol(numcolbaj);
				searchNumRenovacion();

//				getParteRen().setFecconf(getParteRen().getParteit().getFeciniori().+3+((getNumParteRenovacion()-1*7))
				Calendar cal = new GregorianCalendar();
				cal.setTime(((Parteit)pit.get(0)).getId().getFecini());
				cal.add(Calendar.DATE, 3+((Integer.parseInt(getNumParteRenovacion())-1*7)));
				getParteRen().setFecconf(cal.getTime());
			}
			
//			se busca si es un parte de recaida
			Criteria recaidasParteitCriteria = new Criteria();
			recaidasParteitCriteria.addExpression(allParteitCriteria.getExpression());
			recaidasParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), true);
			pit = getParteitBean().getList(recaidasParteitCriteria);
			if(pit.size()>0 && getTrabajadorStatus().equals(ItStatus.ALTA)){
				searchRecaida((Parteit)pit.get(0));
			}
		} else {
			trabajadorStatus = ItStatus.ALTA;
		}
	}
	
	private void searchNumRenovacion() throws ManagerBeanException {
		String numero = Utils.maxCode("Parteconf", "id.numero","cdg="+((Trabajador)getContratos().get(0)).getCdg());
		((Trabajador)getContratos().get(0)).getCdg();
		
		setNumParteRenovacion(numero);
	}

	private void searchRecaida(Parteit pit) throws ManagerBeanException {
		if(pit.getTipoit().equals(Tipoit.ENFERMEDAD) || pit.getTipoit().equals(Tipoit.ACCIDENTE) || pit.getTipoit().equals(Tipoit.NOLABORAL)){
			setRecaida(true);
		}
		
	}
	
	@Override
	public void onReset(ActionEvent arg0) {
		super.onReset(arg0);
		reset();
		setFinished(false);
		setParteRen(null);
		setModel(null);
	}

	@Override
	public void onSelect(ActionEvent arg0) {
		super.onSelect(arg0);
		reset();
	}
	
	@Override
	public void onSearch(ActionEvent arg0) {
		try {
			getCriteria().addNullExpression(getFieldName(IPayrollAlias.TRABAJADOR_FECBAJ));
			getCriteria().addNotNullExpression(getFieldName(IPayrollAlias.TRABAJADOR_EMPRESA_CDG));
			super.onSearch(arg0);
			clearCriteria();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onFinalize(ActionEvent event) {
		// validaciones
		
		// validar si hay o colegiado o cias, uno obligatorio
		// validar la mascara de num cias
		
		checkColOrCias();
		setFinished(true);
		
	}

	public void onCheckCias(ActionEvent event) {
		checkCias();
	}
	
	private void checkCias() {
		if(!Utils.validarMascara(getParteRen().getCias(), "##########A")){
			String msg = "Numero cias no valido. Debe cumplir con la mascara 9999999999X.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void checkColOrCias() {
		if(getParteRen().getCias().isEmpty() && getParteRen().getNumcol().isEmpty()){
			String msg = "Es necesario el numero de cias o colegiado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else if(!getParteRen().getCias().isEmpty()){
			checkCias();
		} else if(getParteRen().getCias().isEmpty()){
			getParteRen().setCias(null);
		}
		
	}

	public void onUnFinalize(ActionEvent event) {
		setFinished(false);
	}
	
	public void onSave(ActionEvent event) {
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			// start transaction
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			HibernateUtil.startSession(sessionName);
			for(ITransferObject to: getContratos()){
				Trabajador t = (Trabajador)to;
				if (getParteRen().getStatus() == ItStatus.BAJA) {
					insertBaja(t);
				} else if (getParteRen().getStatus() == ItStatus.ALTA) {
					insertAlta(t);
				} else {
					insertRenovacion(t);
				}
			}
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			setFinished(true);
			// commit
		} catch (Exception e) {
			// rollback
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
//				LOGGER.error(msg, e);
			}
			String msg = "Error al guardar los partes:  " + e.getMessage() ;
			//LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession( mustCloseSession );
			HibernateUtil.setBeginTransaction( mustBeginTransaction );
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
//		String numero = Utils.maxCode("Parteconf", "id.numero","cdg="+id.getCdg()+" and fecini="+id.getFecini());
//		id.setNumero(Integer.parseInt(numero)+1); //Número parte confirmación ???????????????????????
		id.setNumero(Integer.parseInt(getNumParteRenovacion())+1); //Número parte confirmación ???????????????????????
		p.setId(id);
		p.setCias(getParteRen().getCias());
		p.setFecconf(getParteRen().getFecconf());
		p.setNumcol(getParteRen().getNumcol());
		p.setParproc(getParteRen().getParproc());
		p.setParproc(getParteRen().getParproc());
		p.setParproc("N");
		pit.setRecaida(getRecaida());
		p.setParteit(pit);
		
		getParteconfBean().insertOrUpdate(p);
		
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
