package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.IDataModelDataProvider;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;
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
	private TipoOperacionIT operacion;
	private boolean recaidaAnterior;
	
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
			refreshOperacion();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	private void refreshOperacion() {
		if (getParteIT().getFechaBaja() == null) {
			setOperacion(TipoOperacionIT.BAJA);
		} else {
			setOperacion(TipoOperacionIT.ALTA);
		}
	}
	
	public void onFinish(ActionEvent event) {
		try {
			int err = getParteITDAO().validate(getParteIT());
			if ( err > 0) {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String errorMsg = AonPayroll.getMessage(locale,"aon_payroll_error_" + err);
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
			getParteITDAO().calculate(getParteIT());
			getParteITDAO().accept( getParteIT() );
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
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
		} else if (getCurrentStep() == 1) {
			// Seleccion de persona
		} else if (getCurrentStep() == 2) {
			// Mover al paso tres cuando se haga la validacion
			onFinish(event);
		}
		setCurrentStep(getCurrentStep() + 1);
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
		return (getCurrentStep() < 4);
	}
	
}
