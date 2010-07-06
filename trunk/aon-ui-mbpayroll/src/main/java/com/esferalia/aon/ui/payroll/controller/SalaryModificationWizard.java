package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPercepcion;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;

public class SalaryModificationWizard implements Serializable, IDataModelDataProvider, ICriteriaProvider {

	private static final long serialVersionUID = -841247628893188441L;

	private int currentStep;
	private static final String[] STEPS = { "salaryModificationWizard_step0", "salaryModificationWizard_step1", "salaryModificationWizard_step2", "salaryModificationWizard_step3" };
	private EmpleadoParams params;
	private IEmpleadoDAO empleadoDAO;
	private DataModel empleadoModel;
	private DataModel empleosModel;
	private DataModel percepcionesModel;
	private List<IPercepcion> newPercepcionesList;
	private IPersona persona;
	private ContratoEmpleado contratoEmpleado; 
	

	public ContratoEmpleado getContratoEmpleado() {
		return contratoEmpleado;
	}

	public void setContratoEmpleado(ContratoEmpleado contratoEmpleado) {
		this.contratoEmpleado = contratoEmpleado;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public EmpleadoParams getParams() {
		if (params == null) {
			params = new EmpleadoParams();
			params.setFinalizados(true);
			params.setClienteActivo(true);
		}
		return params;
	}

	public void setParams(EmpleadoParams params) {
		this.params = params;
	}
	
	public IEmpleadoDAO getEmpleadoDAO() {
		if (empleadoDAO == null) {
			empleadoDAO = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
		}
		return empleadoDAO;
	}
	
	public DataModel getEmpleadoModel() {
		if (empleadoModel == null) {
			empleadoModel = new ExtendedPageDataModel(this, this);
		}
		return empleadoModel;
	}

	public void setEmpleadoModel(DataModel empleadoModel) {
		this.empleadoModel = empleadoModel;
	}
	
	public DataModel getEmpleosModel() {
		try {
			if (empleosModel == null) {
				EmpleadoParams ep = new EmpleadoParams();
				ep.setFinalizados(getParams().isFinalizados());
				ep.setPersonaId(Integer.toString(getPersona().getId()));
				List<IEmpleado> list = getEmpleadoDAO().getEmpleados(ep);
				List<ContratoEmpleado> extendedList = transformEmpleadoModel(list);
				empleosModel = new ListDataModel(extendedList);
			}
			return empleosModel;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	public void setEmpleosModel(DataModel empleosModel) {
		this.empleosModel = empleosModel;
	}

	public ArrayList<?> getEmpleos(){
		return (ArrayList<?>)empleosModel.getWrappedData();
	}
	
	private List<ContratoEmpleado> transformEmpleadoModel(List<IEmpleado> list) throws PayrollException {
		List<ContratoEmpleado> extendedList = new ArrayList<ContratoEmpleado>();
		if (list.size() > 0) {
			for (IEmpleado e : list) {
				ContratoEmpleado ce = new ContratoEmpleado();
//				if(list.size()==1){
//					ce.setSelected(true);
//				}
				ce.setEmpleado(e);
				// En la primera iteración incializamos los selected. 
				if (getContratoEmpleado() == null) {
					setContratoEmpleado(ce); 
					onChangeEmpleado();
				}
				extendedList.add(ce);				
			}
		}
		return extendedList;
	}

	private void initializeEmpleadoModel() throws ManagerBeanException {
		if (empleadoModel == null) {
			empleadoModel = new ExtendedPageDataModel(this, this);
		}
		((ExtendedPageDataModel) empleadoModel).update(0, getPageLimit());
	}
	
	public IPersona getPersona() {
		return persona;
	}

	public void setPersona(IPersona persona) {
		this.persona = persona;
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
	@Override
	public Criteria getCriteria() throws ManagerBeanException {
		try {
			return getEmpleadoDAO().getCriteria(getParams());
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
	
	public DataModel getPercepcionesModel() {
		try {
			if (percepcionesModel == null) {
				List<IPercepcion> list = getEmpleadoDAO().getPercepciones(getContratoEmpleado().getEmpleado());
				percepcionesModel = new ListDataModel(transformPercepcionesModel(list));
			}
			return percepcionesModel;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	public void setPercepcionesModel(DataModel percepcionesModel) {
		this.percepcionesModel = percepcionesModel;
	}
	private List<PercepcionEmpleado> transformPercepcionesModel(List<IPercepcion> list) throws PayrollException {
		List<PercepcionEmpleado> extendedList = new ArrayList<PercepcionEmpleado>();
		if (list.size() > 0) {
			for (IPercepcion p : list) {
				PercepcionEmpleado pe = new PercepcionEmpleado();
				pe.setPercepcion(p);
				extendedList.add(pe);				
			}
		}
		return extendedList;
	}
	
	public List<IPercepcion> getNewPercepcionesList() {
		return newPercepcionesList;
	}

	public void setNewPercepcionesList(List<IPercepcion> newPercepcionesList) {
		this.newPercepcionesList = newPercepcionesList;
	}

	private void buildPercepcionesList() throws PayrollException {
		for (int i = 0; i < getPercepcionesModel().getRowCount(); i++) {
			getPercepcionesModel().setRowIndex(i);
			PercepcionEmpleado percepcion = (PercepcionEmpleado) getPercepcionesModel().getRowData();
			IPercepcion oldPercepcion = percepcion.getPercepcion();
			if (percepcion.getImporteNuevo()!=null) {
				Date currentDate = Calendar.getInstance().getTime();
				IPercepcion newPercepcion = getEmpleadoDAO().initializePercepcion(getContratoEmpleado().getEmpleado());
				newPercepcion.setFechaInicio(currentDate);
				newPercepcion.setFechaFin(oldPercepcion.getFechaFin());
				newPercepcion.setFechaRetroactividad(oldPercepcion.getFechaRetroactividad());
				newPercepcion.setDescripcionComplemento(oldPercepcion.getDescripcionComplemento());
				newPercepcion.setDescripcionAbreviada(oldPercepcion.getDescripcionAbreviada());
				newPercepcion.setFormaCalculo(oldPercepcion.getFormaCalculo());
				newPercepcion.setTipoCotizacion(oldPercepcion.getTipoCotizacion());
				newPercepcion.setUnidades(oldPercepcion.getUnidades());
				newPercepcion.setImporteUnitario(oldPercepcion.getImporteUnitario());
				newPercepcion.setImporte(percepcion.getImporteNuevo());
				newPercepcion.setMes(oldPercepcion.getMes());
				newPercepcion.setGarantizadoILT(oldPercepcion.getGarantizadoILT());
				newPercepcion.setRedondeoPagaExtra(oldPercepcion.getRedondeoPagaExtra());
				newPercepcion.setFechaCreacion(currentDate);
				newPercepcion.setHoraCreacion(currentDate);
				newPercepcion.setFechaModificacion(null);
				newPercepcion.setHoraModificacion(null);
				newPercepcion.setEmpleado(oldPercepcion.getEmpleado());
				newPercepcion.setComplemento(oldPercepcion.getComplemento());
				newPercepcion.setComplementoAplicar(oldPercepcion.getComplementoAplicar());
				newPercepcion.setFijoVariable(oldPercepcion.getFijoVariable());
				newPercepcion.setIndiceComplemento(oldPercepcion.getIndiceComplemento());
				newPercepcion.setTipoComplemento(oldPercepcion.getTipoComplemento());
				newPercepcion.setRetribucion(oldPercepcion.getRetribucion());
				getNewPercepcionesList().add(newPercepcion);
//				oldPercepcion.setFechaFin(currentDate);
			}
		}
	}	

	
//	Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
//			onDiskGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
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

	// ***************************************************
	public void onStart(ActionEvent event) {
		params = null;
		setCurrentStep(0);
		setEmpleadoModel(null);
		setPercepcionesModel(null);
	}
	
	private void onValidate(ActionEvent event) {
		try{
			if(!isAnyPercepcionSelected()){
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String errorMsg = AonPayroll.getMessage(locale, "aon_payroll_error_no_percepcion_selected");
				throw new PayrollException(errorMsg);
			}
			setNewPercepcionesList(new ArrayList<IPercepcion>());
			buildPercepcionesList();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private boolean isAnyPercepcionSelected() {
		for (int i = 0; i < getPercepcionesModel().getRowCount(); i++) {
			getPercepcionesModel().setRowIndex(i);
			PercepcionEmpleado percepcion = (PercepcionEmpleado) getPercepcionesModel().getRowData();
			if (percepcion.getImporteNuevo()!=null) {
				return true;
			}
		}
		return false;
	}

	private void onFinish(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				Date currentDate = Calendar.getInstance().getTime();
				// actualizar percepciones modificadas
				for (int i = 0; i < getPercepcionesModel().getRowCount(); i++) {
					getPercepcionesModel().setRowIndex(i);
					PercepcionEmpleado percepcion = (PercepcionEmpleado) getPercepcionesModel().getRowData();
					if (percepcion.getImporteNuevo()!=null) {
						percepcion.getPercepcion().setFechaFin(currentDate);
						getEmpleadoDAO().accept(percepcion.getPercepcion());
					}
				}
				// insertar percepciones nuevas
				for (int i = 0; i < getNewPercepcionesList().size(); i++) {
					IPercepcion percepcion = getNewPercepcionesList().get(i);
					getEmpleadoDAO().accept(percepcion);
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
		setPercepcionesModel(null);
		setCurrentStep(2);
		setContratoEmpleado(null);
		setEmpleosModel(null);
	}
	
	public void onSelectContrato(ActionEvent event) {
		ContratoEmpleado emp = (ContratoEmpleado) getEmpleosModel().getRowData();
		setContratoEmpleado(emp);
		onChangeEmpleado();
	}
	
	private void onChangeEmpleado() {
		setPercepcionesModel(null);
	}

}
