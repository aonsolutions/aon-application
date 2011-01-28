package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.payroll.core.empresa.RemesaCertificadoEmpresaParams;
import com.esferalia.aon.payroll.core.enumeration.CausaSuspension;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;

public class RemesaCertificadoGenerationWizard implements Serializable {

	private static final long serialVersionUID = -8284038117326971930L;

	private int currentStep;
	private static final String[] STEPS = {
			"remesaCertificadoGenerationWizard_step0",
			"remesaCertificadoGenerationWizard_step1",
			"remesaCertificadoGenerationWizard_step2" };
	private RemesaCertificadoEmpresaParams params;
	private DataModel model;
	private DataModel selectedModel;
	private IEmpresaDAO empresaDAO;
	private List<IRemesaCertificadoEmpresa> listaRemesas;
	private List<IRemesaCertificadoEmpresa> savedRemesas;
	private boolean remesable;
	private CausaSuspension suspensionCauseForAll;
	
	public CausaSuspension getSuspensionCauseForAll() {
		return suspensionCauseForAll;
	}

	public void setSuspensionCauseForAll(CausaSuspension suspensionCauseForAll) {
		this.suspensionCauseForAll = suspensionCauseForAll;
//		aplyAllSuspensionCause(suspensionCauseForAll);
	}

	public boolean isRemesable() {
		return remesable;
	}

	public void setRemesable(boolean remesable) {
		this.remesable = remesable;
	}

	public RemesaCertificadoEmpresaParams getParams() {
		if (params == null) {
			params = new RemesaCertificadoEmpresaParams();
		}
		return params;
	}

	public void setParams(RemesaCertificadoEmpresaParams params) {
		this.params = params;
	}

	public List<IRemesaCertificadoEmpresa> getListaRemesas() {
		if (listaRemesas == null) {
			listaRemesas = new ArrayList<IRemesaCertificadoEmpresa>();
		}
		return listaRemesas;
	}

	public void setListaRemesas(List<IRemesaCertificadoEmpresa> listaRemesas) {
		this.listaRemesas = listaRemesas;
	}
	public List<IRemesaCertificadoEmpresa> getSavedRemesas() {
		if (savedRemesas == null) {
			savedRemesas = new ArrayList<IRemesaCertificadoEmpresa>();
		}
		return savedRemesas;
	}
	
	public void setSavedRemesas(List<IRemesaCertificadoEmpresa> savedRemesas) {
		this.savedRemesas = savedRemesas;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public IEmpresaDAO getEmpresaDAO() {
		if (empresaDAO == null) {
			empresaDAO = EmpresaDAOFactory.getInstance().getEmpresaDAO();
		}
		return empresaDAO;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}

	public DataModel getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	private void initializeModel() throws PayrollException {
		List<RemesableEmpleadoCertificate> list = transformList(getEmpresaDAO()
				.getEmpleados(params));
		setModel(new ListDataModel(list));
	}

	private List<RemesableEmpleadoCertificate> transformList(
			List<IEmpleado> empleados) {
		List<RemesableEmpleadoCertificate> list = new ArrayList<RemesableEmpleadoCertificate>();
		for (IEmpleado e : empleados) {
			RemesableEmpleadoCertificate r = new RemesableEmpleadoCertificate();
			r.setEmpleado(e);
			list.add(r);
		}
		return list;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
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
		return (getCurrentStep() < 2);
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setParams(null);
		getParams().setFechaHasta(Calendar.getInstance().getTime());
		setModel(null);
		setSelectedModel(null);
		setListaRemesas(null);
		setRemesable(true);
		setCurrentStep(0);
	}

	private void onValidate(ActionEvent event) {
		if (!isAnyEmpleadoSelected()) {
			String msg = "Debe seleccionar algún empleado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			generateRemesasList();
		} catch (PayrollException e) {
			throw new AbortProcessingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void generateRemesasList() throws PayrollException{
		IEmpleado empleado;
		setListaRemesas(null);
		List<RemesableEmpleadoCertificate> list = new LinkedList<RemesableEmpleadoCertificate>();
		for (RemesableEmpleadoCertificate remesable : (List<RemesableEmpleadoCertificate>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				if (!isCausaSuspensionSelected(remesable)) {
					String msg = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				list.add(remesable);
				empleado = remesable.getEmpleado();
				addEmpresaToRemesasList(empleado);
			}
		}
		setSelectedModel(new ListDataModel(list));
	}
	
	private void addEmpresaToRemesasList(IEmpleado empleado) throws PayrollException {
		if (!isEmpresaInList(empleado.getEmpresa())) {
			List<IRemesaCertificadoEmpresa> remesas = getExistingRemesas(empleado);
			if(remesas.size()>0){
				IRemesaCertificadoEmpresa remesa = findRemesa(remesas, empleado);
				if(remesa!=null){
					getListaRemesas().add(remesa);
				} else {
					getListaRemesas().add(getEmpresaDAO().getNewRemesa(empleado));
				}
			} else {
				getListaRemesas().add(getEmpresaDAO().getNewRemesa(empleado));
			}
		}
	}
	
	private IRemesaCertificadoEmpresa findRemesa(
			List<IRemesaCertificadoEmpresa> remesas, IEmpleado empleado) {
		for(IRemesaCertificadoEmpresa remesa: remesas){
			if(empleado.getFechaFin().before(Calendar.getInstance().getTime())){
				if(remesa.getFecha().before(Calendar.getInstance().getTime())){
					return remesa;
				}
			} else {
				if(empleado.getFechaFin().equals(new Date(remesa.getFecha().getTime()-(1*24*60*60*1000)))){
					return remesa;
				}
			}
		}
		return null;
	}

	private List<IRemesaCertificadoEmpresa> getExistingRemesas(IEmpleado empleado) throws PayrollException {
		RemesaCertificadoEmpresaParams params = new RemesaCertificadoEmpresaParams();
		FileStatus[] estados = {FileStatus.PENDIENTE};
		params.setEmpresa(empleado.getEmpresa().getName());
		params.setEstados(estados);
		return getEmpresaDAO().getRemesaCertificados(params);
	}

	private boolean isCausaSuspensionSelected(RemesableEmpleadoCertificate remesable) {
		return remesable.getCausaSuspension()!=null;
	}

	public boolean isAnyEmpleadoSelected() {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel().getRowData();
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}

	private boolean isEmpresaInList(IEmpresa empresa) {
		Iterator<?> iterator = getListaRemesas().iterator();
		while (iterator.hasNext()) {
			IRemesaCertificadoEmpresa remesa = (IRemesaCertificadoEmpresa) iterator
					.next();
			if (remesa.getEmpresa().getId().equals(empresa.getId())) {
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
				for (IRemesaCertificadoEmpresa r : getListaRemesas()) {
					IRemesaCertificadoEmpresa remesa = getEmpresaDAO()
							.accept(r);
					getSavedRemesas().add(remesa);
					List<IRemesaCertificadoEmpresaDetalle> list = getRemesaDetalleList(remesa);
					for (IRemesaCertificadoEmpresaDetalle d : list) {
						getEmpresaDAO().accept(d);
					}
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
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

		onStart(event);
	}

	@SuppressWarnings("unchecked")
	private List<IRemesaCertificadoEmpresaDetalle> getRemesaDetalleList(
			IRemesaCertificadoEmpresa remesa) {
		List<IRemesaCertificadoEmpresaDetalle> list = new ArrayList<IRemesaCertificadoEmpresaDetalle>();

		for (RemesableEmpleadoCertificate d : (List<RemesableEmpleadoCertificate>) getSelectedModel()
				.getWrappedData()) {
			if (d.getEmpleado().getEmpresa().getId().equals(remesa.getEmpresa().getId())) {
				IRemesaCertificadoEmpresaDetalle detalle = getEmpresaDAO()
						.getNewRemesaDetalle(d.getEmpleado());
				detalle.setRemesaCertificado(remesa);
				detalle.getEmpleado().setId(d.getEmpleado().getId().intValue());
				detalle.setFechaBaja(d.getEmpleado().getFechaFin());
				detalle.setCausaSuspension(d.getCausaSuspension());
				list.add(detalle);
			}
		}
		return list;
	}

	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}

	private void onSearch(ActionEvent event) {
		try {
			initializeModel();
			setListaRemesas(null);
		} catch (PayrollException e) {
			// NADA
		}
	}

	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel()
					.getRowData();
			r.setSelected(selected);
		}
	}
	
	public void onChangeRemesableDate(ActionEvent event) {
		if(getParams().getFechaHasta().after(Calendar.getInstance().getTime())){
			setRemesable(false);
		} else {
			setRemesable(true);
		}
	}

	public void onChangeWizard(ActionEvent event) {
		this.onNext(event);
		RemesaCertificadoWizard wizard = (RemesaCertificadoWizard) AonUtil.getRegisteredBean("remesaCertificadoWizard");
		wizard.setCurrentStep(1);
		wizard.initializeRemesasModel(getSavedRemesas());
		setSavedRemesas(null);
	}
	
	public void onApplyAllSuspensionCause(ActionEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
	}
	
	private void applyAllSuspensionCause(CausaSuspension suspensionCause) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel()
			.getRowData();
			if(r.isSelected()){
				r.setCausaSuspension(suspensionCause);
			}
		}
	}

}
