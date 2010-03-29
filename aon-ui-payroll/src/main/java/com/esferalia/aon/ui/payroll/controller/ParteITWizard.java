package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.IDataModelDataProvider;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IConfirmacionParteIT;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.ui.payroll.enumeration.TipoOperacionIT;

public class ParteITWizard implements Serializable, IDataModelDataProvider,ICriteriaProvider {
	
	private static final long serialVersionUID = -6091663393601321263L;
	
	private IEmpleadoDAO empleadoDAO;
	private IParteITDAO parteITDAO;
	private EmpleadoParams params;
	private DataModel empleadoModel;
	private IPersona persona;
	private IEmpleado empleado;
	private DataModel empleosModel; 
	private DataModel partesModel; 
	private int currentStep;
	private static final String[] STEPS = { "parteITWizard_step0","parteITWizard_step1","parteITWizard_step2","parteITWizard_step3" };
	private IParteIT parteIT;
	private IConfirmacionParteIT confirmacionParteIT;
	private TipoOperacionIT operacion;
	private boolean recaidaAnterior;
	private Integer numParteRenovacion;
	
	public IConfirmacionParteIT getConfirmacionParteIT() {
		return confirmacionParteIT;
	}
	public void setConfirmacionParteIT(IConfirmacionParteIT confirmacionParteIT) {
		this.confirmacionParteIT = confirmacionParteIT;
	}

	public Integer getNumParteRenovacion() {
		return numParteRenovacion;
	}
	public void setNumParteRenovacion(Integer numParteRenovacion) {
		this.numParteRenovacion = numParteRenovacion;
	}
	
	public boolean isRecaidaAnterior() {
		return recaidaAnterior;
	}
	public void setRecaidaAnterior(boolean recaidaAnterior) {
		this.recaidaAnterior = recaidaAnterior;
	}
	public IParteIT getParteIT() {
		return parteIT;
	}
	public void setParteIT(IParteIT parteIT) {
		this.parteIT = parteIT;
	}
	public IEmpleadoDAO getEmpleadoDAO() {
		if (empleadoDAO == null) {
			empleadoDAO = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
		}
		return empleadoDAO;
	}
	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}

	public EmpleadoParams getParams() {
		if (params == null) {
			params = new EmpleadoParams();
			params.setFinalizados(true);
		}
		return params;
	}
	public void setParams(EmpleadoParams params) {
		this.params = params;
	}

	public DataModel getEmpleadoModel() {
		if (empleadoModel == null) {
			empleadoModel = new ExtendedPageDataModel(this, this);	
		}
		return empleadoModel;
	}

	public IPersona getPersona() {
		return persona;
	}
	public void setPersona(IPersona persona) {
		this.persona = persona;
	}
	
	public IEmpleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}

	public TipoOperacionIT getOperacion() {
		return operacion;
	}
	public void setOperacion(TipoOperacionIT operacion) {
		this.operacion = operacion;
	}
	
	public String getCias() {
		return getOperacion() == TipoOperacionIT.BAJA?getParteIT().getCiasBaja():getParteIT().getCiasAlta();
	}
	public void setCias(String cias) {
		if(StringUtils.isBlank(cias)){
			cias=null;
		}
		if (getOperacion() == TipoOperacionIT.BAJA) {
			getParteIT().setCiasBaja(cias);
		} else if (getOperacion() == TipoOperacionIT.ALTA) {
			getParteIT().setCiasAlta(cias);
		} else if (getOperacion() == TipoOperacionIT.CONFIRMACION) {
			getConfirmacionParteIT().setCias(cias);
		}
	}

	public String getNumeroColegiado() {
		return getOperacion() == TipoOperacionIT.BAJA?getParteIT().getNumeroColegiadoBaja():getParteIT().getNumeroColegiadoAlta();
	}
	public void setNumeroColegiado(String numeroColegiado) {
		if(StringUtils.isBlank(numeroColegiado)){
			numeroColegiado=null;
		}
		if (getOperacion() == TipoOperacionIT.BAJA) {
			getParteIT().setNumeroColegiadoBaja(numeroColegiado);
		} else if (getOperacion() == TipoOperacionIT.ALTA) {
			getParteIT().setNumeroColegiadoAlta(numeroColegiado);
		} else if (getOperacion() == TipoOperacionIT.CONFIRMACION) {
			getConfirmacionParteIT().setNumeroColegiado(numeroColegiado);
		}
	}

	public Date getFecha() {
		return getOperacion() == TipoOperacionIT.BAJA?getParteIT().getFechaBaja():getParteIT().getFechaAlta();
	}
	public void setFecha(Date fecha) {
		if (getOperacion() == TipoOperacionIT.BAJA) {
			getParteIT().setFechaBaja(fecha);
		} else if (getOperacion() == TipoOperacionIT.ALTA) {
			getParteIT().setFechaAlta(fecha);
		} else if (getOperacion() == TipoOperacionIT.CONFIRMACION) {
			getConfirmacionParteIT().setFecha(fecha);
		}
	}

	public DataModel getEmpleosModel() {
		try {
			if (empleosModel == null) {
				EmpleadoParams ep = new EmpleadoParams();
				ep.setFinalizados(false);
				ep.setPersonaId( Integer.toString( getPersona().getId()));
				List<IEmpleado> list = getEmpleadoDAO().getEmpleados(ep);
				if (list.size()>0) {
					setEmpleado(list.get(0));
					onChangeEmpleado();
				}
				empleosModel = new ListDataModel(list); 
			}
			return empleosModel;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}			
	}

	private void initializeEmpleadoModel() throws ManagerBeanException {
		if (empleadoModel == null) {
			empleadoModel = new ExtendedPageDataModel(this, this);
		}
		((ExtendedPageDataModel) empleadoModel).update(0,getPageLimit());
	}

	public void setEmpleadoModel(DataModel empleadoModel) {
		this.empleadoModel = empleadoModel;
	}

	public DataModel getPartesModel() {
		try {
			if (partesModel == null) {
				List<IParteIT> list = getParteITDAO().getPartesEmpleado(getEmpleado());
				partesModel = new ListDataModel(list); 
			}
			return partesModel;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}			
		
	}

	public void setPartesModel(DataModel partesModel) {
		this.partesModel = partesModel;
	}

	// Action Listeners
	public void onStart(ActionEvent event) {
		params = null;
		setCurrentStep(0);
		setEmpleadoModel(null);
	}
	public void onSearch(ActionEvent event) {
		try {
			initializeEmpleadoModel();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}		
	}
	public void onSelect(ActionEvent event) {
		IPersona persona = (IPersona) getEmpleadoModel().getRowData();
		setPersona(persona);
		empleosModel = null;
		setCurrentStep(2);
	}
	
	public void onSelectEmpleado(ActionEvent event) {
		IEmpleado empleado = (IEmpleado) getEmpleosModel().getRowData();
		setEmpleado(empleado);
		onChangeEmpleado();	
	}
	private void onChangeEmpleado() {
		try {
			partesModel = null;
			setParteIT( getParteITDAO().initialize( getEmpleado()) );
			setConfirmacionParteIT(getParteITDAO().initialize( getParteIT()));
			refreshOperacion();
			searchNumeroRenovacion();
			completeComfirmationDate();
			searchRecaidaAnterior();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshOperacion() {
		if (getParteIT().getFechaBaja() == null) {
			setOperacion(TipoOperacionIT.BAJA);
		} else {
			setOperacion(TipoOperacionIT.ALTA);
		}
	}
	
	public void onValidate(ActionEvent event) {
		try {
			int err = getParteITDAO().validate(getParteIT());
			if ( err > 0) {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String errorMsg = AonPayroll.getMessage(locale,"aon_payroll_error_" + err);
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
			getParteITDAO().calculate(getParteIT());
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}		
	}
	
	public void onFinish(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				if(getOperacion().equals(TipoOperacionIT.CONFIRMACION)){
					getParteITDAO().accept( getConfirmacionParteIT() );
				} else {
					getParteITDAO().accept( getParteIT() );
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				onStart(event);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	@Override
	public int getPageLimit() {
		return 20;
	}
	@Override
	public int getRowCount() throws ManagerBeanException {
		try {
			return getEmpleadoDAO().getCount(getParams());
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		try {
			List<?> list = getEmpleadoDAO().getDistinctEmpleados(getParams(), start, count);
			return (List<ITransferObject>) list;
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}

	@Override
	public Criteria getCriteria() throws ManagerBeanException {
		try {
			return getEmpleadoDAO().getCriteria(getParams());
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}

	//********************
	public int getCurrentStep() {
		return this.currentStep;
	}
	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep; 
	}
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			// Seleccion de persona
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			// Validacion de la IT
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);			
		} else if (getCurrentStep() == 3) {
			onFinish(event);
		}
	}
	
	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}
	public String previous() {
		return STEPS[getCurrentStep()];
	}
	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 3);
	}
	
	public void onChangeFechaInicio( ActionEvent event ) {
		try {
			IContrato  contrato = getParteITDAO().getContrato(getParteIT());
			getParteIT().setProrrateoCotizacion( contrato.getProrrateoCotizacion() );
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onChangeTipoContingencia( ActionEvent event ) {
		searchRecaidaAnterior();
	}
	
	private void searchRecaidaAnterior(){
		try {
			List<IParteIT> list = getParteITDAO().getPartesEmpleado(getEmpleado());
			if (list.size() > 0 && getOperacion().equals(TipoOperacionIT.BAJA)) {
				IParteIT ultimoParte = list.get(0);
				
				if((getParteIT().getTipoContingencia().equals(TipoContingencia.ENFERMEDAD_COMUN) 
						|| getParteIT().getTipoContingencia().equals(TipoContingencia.ACCIDENTE_LABORAL) 
						|| getParteIT().getTipoContingencia().equals(TipoContingencia.ACCIDENTE_NO_LABORAL))
						&& (getParteIT().getTipoContingencia().equals(ultimoParte.getTipoContingencia()))){
					setRecaidaAnterior(true);
				} else {
					setRecaidaAnterior(false);
				}
			} else {
				setRecaidaAnterior(false);
			}
		} catch (PayrollException e) {
			e.printStackTrace();
		}
	}
	
	public void onChangeTipoOperacion( ActionEvent event ) {
		try {
			searchNumeroRenovacion();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (PayrollException e) {
			e.printStackTrace();
		}
	}

	private void searchNumeroRenovacion() throws NumberFormatException, ManagerBeanException, PayrollException {
		if(!getOperacion().equals(TipoOperacionIT.BAJA)){
			setNumParteRenovacion(maxParteconfCode()+1);
			getConfirmacionParteIT().setNumero(getNumParteRenovacion());
		} else {
			setNumParteRenovacion(null);
		}
	}
	
	private Integer maxParteconfCode() throws ManagerBeanException, PayrollException{
		List<IParteIT> listaPartes = getParteITDAO().getPartesEmpleado(getEmpleado());
		IParteIT ultimoParte = listaPartes.get(0);
		
		List<IConfirmacionParteIT> list = getParteITDAO().getPartesConfirmacion(ultimoParte);
		if(list.size()>0){
			return list.get(0).getNumero();
		} else {
			return 0;
		}
	}
	
	public List<SelectItem> getTiposOperacion() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> ops=null;
		if(getOperacion().equals(TipoOperacionIT.ALTA) || getOperacion().equals(TipoOperacionIT.CONFIRMACION)){
			ops = new LinkedList<SelectItem>();
			String name = TipoOperacionIT.CONFIRMACION.getName(locale);
			SelectItem item = new SelectItem(TipoOperacionIT.CONFIRMACION, name);
			ops.add(item);
			name = TipoOperacionIT.ALTA.getName(locale);
			item = new SelectItem(TipoOperacionIT.ALTA, name);
			ops.add(item);
		}
		return ops;
	}
	
	private void completeComfirmationDate(){
		Calendar cal = new GregorianCalendar();
		cal.setTime(getParteIT().getFechaBaja());
		cal.add(Calendar.DAY_OF_YEAR, 3+(((getNumParteRenovacion()-1)*7)));
		getConfirmacionParteIT().setFecha(cal.getTime());
		setFecha(cal.getTime());
	}
	
}
