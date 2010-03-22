package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.IDataModelDataProvider;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;

public class ParteITWizard implements Serializable, IDataModelDataProvider,ICriteriaProvider {
	
	private static final long serialVersionUID = -6091663393601321263L;
	
	private IEmpleadoDAO empleadoDAO;
	private EmpleadoParams params;
	private DataModel empleadoModel;
	private IPersona persona;
	private DataModel empleosModel; 

	private int currentStep;
	private static final String[] STEPS = { "parteITWizard_step0","parteITWizard_step1","parteITWizard_step2","parteITWizard_step3" };
	
	public IEmpleadoDAO getEmpleadoDAO() {
		if (empleadoDAO == null) {
			empleadoDAO = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
		}
		return empleadoDAO;
	}

	public EmpleadoParams getParams() {
		if (params == null) {
			params = new EmpleadoParams();
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
	
	public DataModel getEmpleosModel() {
		try {
			if (empleosModel == null) {
				EmpleadoParams ep = new EmpleadoParams();
				ep.setPersonaId( Integer.toString( getPersona().getId()));
				List<IEmpleado> list = getEmpleadoDAO().getEmpleados(ep);
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

	// Action Listeners
	public void onStart(ActionEvent event) {
		params = new EmpleadoParams();
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
		setCurrentStep(getCurrentStep() + 1);
		if (getCurrentStep() == 0) {
			// nada, no se borran los parámetros.
		} else if (getCurrentStep() == 1) {
			onSearch(event);
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
		return (getCurrentStep() < 4);
	}
	
}
