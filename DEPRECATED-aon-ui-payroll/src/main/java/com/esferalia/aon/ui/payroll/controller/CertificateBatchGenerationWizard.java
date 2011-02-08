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
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Contract;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCertificate;
import com.esferalia.aon.payroll.EnterpriseCertificateDetail;
import com.esferalia.aon.payroll.PayrollException;

import com.esferalia.aon.payroll.enumeration.FileStatus;

public class CertificateBatchGenerationWizard implements Serializable {

	private static final long serialVersionUID = -647409036962869418L;

	private int currentStep;
	private static final String[] STEPS = {
			"certificateBatchGenerationWizard_step0",
			"certificateBatchGenerationWizard_step1",
			"certificateBatchGenerationWizard_step2" };
	private CertificateBatchParams params;
	private DataModel model;
	private DataModel selectedModel;
	private List<EnterpriseCertificate> batchList;
	private List<EnterpriseCertificate> savedBatch;
	private boolean remesable;

	public boolean isRemesable() {
		return remesable;
	}

	public void setRemesable(boolean remesable) {
		this.remesable = remesable;
	}

	public CertificateBatchParams getParams() {
		if (params == null) {
			params = new CertificateBatchParams();
		}
		return params;
	}

	public void setParams(CertificateBatchParams params) {
		this.params = params;
	}

	public List<EnterpriseCertificate> getBatchList() {
		if (batchList == null) {
			batchList = new ArrayList<EnterpriseCertificate>();
		}
		return batchList;
	}

	public void setBatchList(List<EnterpriseCertificate> batchList) {
		this.batchList = batchList;
	}
	public List<EnterpriseCertificate> getSavedBatch() {
		if (savedBatch == null) {
			savedBatch = new ArrayList<EnterpriseCertificate>();
		}
		return savedBatch;
	}
	
	public void setSavedBatch(List<EnterpriseCertificate> savedBatch) {
		this.savedBatch = savedBatch;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public CertificateController getCertificateController() {
		return (CertificateController)AonUtil.getRegisteredBean("enterpriseCertificate");
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
		List<RemesableEmpleadoCertificate> list = transformList(getCertificateController()
				.getEmpleados(params));
		setModel(new ListDataModel(list));
	}

	private List<RemesableEmpleadoCertificate> transformList(
			List<Contract> empleados) {
		List<RemesableEmpleadoCertificate> list = new ArrayList<RemesableEmpleadoCertificate>();
		for (Contract c : empleados) {
			RemesableEmpleadoCertificate r = new RemesableEmpleadoCertificate();
			r.setContract(c);
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
		getParams().setEndDate(Calendar.getInstance().getTime());
		setModel(null);
		setSelectedModel(null);
		setBatchList(null);
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
			generateBatchList();
		} catch (PayrollException e) {
			throw new AbortProcessingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void generateBatchList() throws PayrollException{
		Contract empleado;
		setBatchList(null);
		List<RemesableEmpleadoCertificate> list = new LinkedList<RemesableEmpleadoCertificate>();
		for (RemesableEmpleadoCertificate remesable : (List<RemesableEmpleadoCertificate>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				if (!isSuspensionCauseSelected(remesable)) {
					String msg = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				list.add(remesable);
				empleado = remesable.getContract();
				addEnterpriseToBatchList(empleado);
			}
		}
		setSelectedModel(new ListDataModel(list));
	}
	
	private void addEnterpriseToBatchList(Contract empleado) throws PayrollException {
		if (!isEenterpriseInList(empleado.getWorkPlace().getEnterprise())) {
			List<EnterpriseCertificate> remesas = getExistingBatchList(empleado);
			if(remesas.size()>0){
				EnterpriseCertificate remesa = findBatch(remesas, empleado);
				if(remesa!=null){
					getBatchList().add(remesa);
				} else {
					getBatchList().add(getCertificateController().getNewBatch(empleado));
				}
			} else {
				getBatchList().add(getCertificateController().getNewBatch(empleado));
			}
		}
	}
	
	private EnterpriseCertificate findBatch(
			List<EnterpriseCertificate> remesas, Contract empleado) {
		for(EnterpriseCertificate remesa: remesas){
			if(empleado.getEndDate().before(Calendar.getInstance().getTime())){
				if(remesa.getDate().before(Calendar.getInstance().getTime())){
					return remesa;
				}
			} else {
				if(empleado.getEndDate().equals(new Date(remesa.getDate().getTime()-(1*24*60*60*1000)))){
					return remesa;
				}
			}
		}
		return null;
	}

	private List<EnterpriseCertificate> getExistingBatchList(Contract empleado) throws PayrollException {
		CertificateBatchParams params = new CertificateBatchParams();
		FileStatus[] estados = {FileStatus.PENDIENTE};
		params.setEnterprise(empleado.getWorkPlace().getEnterprise().getRegistry().getName());
		params.setStatusList(estados);
		return getCertificateController().getCertificateBatchList(params);
	}

	private boolean isSuspensionCauseSelected(RemesableEmpleadoCertificate remesable) {
		return remesable.getSuspensionCause()!=null;
	}

	private boolean isAnyEmpleadoSelected() {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel().getRowData();
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}

	private boolean isEenterpriseInList(Enterprise empresa) {
		Iterator<?> iterator = getBatchList().iterator();
		while (iterator.hasNext()) {
			EnterpriseCertificate remesa = (EnterpriseCertificate) iterator
					.next();
			if (remesa.getEnterprise().getId().equals(empresa.getId())) {
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
				for (EnterpriseCertificate r : getBatchList()) {
					EnterpriseCertificate remesa = getCertificateController()
							.accept(r);
					getSavedBatch().add(remesa);
					List<EnterpriseCertificateDetail> list = getDetailBatchList(remesa);
					for (EnterpriseCertificateDetail d : list) {
						getCertificateController().accept(d);
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
	private List<EnterpriseCertificateDetail> getDetailBatchList(
			EnterpriseCertificate remesa) {
		List<EnterpriseCertificateDetail> list = new ArrayList<EnterpriseCertificateDetail>();

		for (RemesableEmpleadoCertificate d : (List<RemesableEmpleadoCertificate>) getSelectedModel()
				.getWrappedData()) {
			if (d.getContract().getWorkPlace().getEnterprise().getId().equals(remesa.getEnterprise().getId())) {
				EnterpriseCertificateDetail detalle = getCertificateController()
						.getNewDetailBatch(d.getContract());
				detalle.setEnterpriseCertificate(remesa);
				detalle.getContract().setId(d.getContract().getId().intValue());
				detalle.setExpireDate(d.getContract().getEndDate());
				detalle.setSuspensionCause(d.getSuspensionCause());
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
			setBatchList(null);
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
		if(getParams().getEndDate().after(Calendar.getInstance().getTime())){
			setRemesable(false);
		} else {
			setRemesable(true);
		}
	}

	public void onChangeWizard(ActionEvent event) {
		this.onNext(event);
		CertificateFileGenerationWizard wizard = (CertificateFileGenerationWizard) AonUtil.getRegisteredBean("certificateFileGenerationWizard");
		wizard.setCurrentStep(1);
		wizard.initializeBatchModel(getSavedBatch());
		setSavedBatch(null);
	}

}
