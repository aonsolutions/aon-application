package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class Certifica2BatchController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private FileOutput fileOutput;
	private boolean recorded;
	private Certifica2BatchNewWizard newBatchWizard;
	
	private boolean showCommunicationWindow;
	
	
	public Certifica2BatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new Certifica2BatchNewWizard(this);
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(Certifica2BatchNewWizard newBatchWizard) {
		this.newBatchWizard = newBatchWizard;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public boolean isRecorded() {
		return recorded;
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}
	
	public boolean isShowCommunicationWindow() {
		return showCommunicationWindow;
	}

	public void setShowCommunicationWindow(boolean showCommunicationWindow) {
		this.showCommunicationWindow = showCommunicationWindow;
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
        listController.checkAllSuspensionCauses();
		
		List<Contract> duplicatedList = new LinkedList<Contract>();
		List<Integer> includedPersons = new LinkedList<Integer>();
        for (Integer contractId: listController.getBatchDetailList().keySet()) {
        	Certifica2BatchDetail detail = (Certifica2BatchDetail) listController.getBatchDetailList().get(contractId);
			if(includedPersons.contains(detail.getContract().getPerson().getId())){
				duplicatedList.add(detail.getContract());
			} else {
				detail.setDomain(detail.getContract().getDomain());
				detail.setStatus(FileStatus.PENDING);
				detail.setCertifica2Batch((Certifica2Batch) getTo());
				certifica2BatchDetailBean.insert(detail);
				includedPersons.add(detail.getContract().getPerson().getId());
			}
		}
		if(duplicatedList.size()>0){
			AonUtil.addErrorMessage("Se han encontrado personas duplicadas. No se incluyen en la remesa.");
			for(Contract contract: duplicatedList){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String period = "(" + formatter.format(contract.getStartDate());
				period += " - ";
				period += formatter.format(contract.getEndDate()) + ")";
				AonUtil.addErrorMessage(contract.getPerson().getFullName() + " " + period);
			}
		}
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        updateBatchEnterprise();
        onSearchContracts(event);
	}

	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController certifica2BatchDetailController = (BatchDetailController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = certifica2BatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	Certifica2BatchDetail certifica2BatchDetail = (Certifica2BatchDetail) iterator.next();
        	contractBean.update(certifica2BatchDetail.getContract());
        	certifica2BatchDetailBean.remove(certifica2BatchDetail);
        }
        certifica2BatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        removeBatchEnterprise();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }

	private void updateBatchEnterprise() {
		Certifica2Batch batch = (Certifica2Batch) this.getTo();
		SEPEUtils utils = SEPEUtils.getInstance();
		if(batch!=null && (batch.getEnterprise()==null || batch.getEnterprise().getId()==null || batch.getEnterprise().equals(utils.getCurrentDomainEnterprise()))){
			try {
				LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
				if(batchDetailController.getRowCount()>0){
					batch.setEnterprise(((Certifica2BatchDetail)batchDetailController.getWrappedList().get(0)).getContract().getWorkPlace().getEnterprise());
					this.accept(null);
				}
			} catch (ManagerBeanException e) {
				AonUtil.addWarningMessage("No se ha podido actualizar la empresa del certificado.");
			}
		}
	}
	
	private void removeBatchEnterprise() {
		try {
			LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
			if(batchDetailController.getRowCount()<=0){
				SEPEUtils utils = SEPEUtils.getInstance();
				Certifica2Batch batch = (Certifica2Batch) this.getTo();
				batch.setEnterprise(utils.getCurrentDomainEnterprise());
				this.accept(null);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addWarningMessage("No se ha podido actualizar la empresa del certificado.");
		}
	}
	
	public void onSearchContracts(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		onEditSearchList(event);
		listController.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		listController.clearCriteria();
		listController.onEditSearch(event);
		Certifica2Batch batch = (Certifica2Batch) this.getTo();
		SEPEUtils utils = SEPEUtils.getInstance();
		if(DomainManager.isDomainManagementAvailable() && batch!=null
				&& !batch.getEnterprise().equals(utils.getCurrentDomainEnterprise()) ){
			listController.init(batch.getEnterprise());
		} else {
			listController.init(null);
		}
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	public void onCreateDisk(ActionEvent event) {
		CertificadosController certificados = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
		certificados.initialize((Certifica2Batch) this.getTo());
		certificados.onCertificadosAccept(event);
	}
	
	public void onRemoveFile(ActionEvent event) throws ManagerBeanException{
		Certifica2BatchAttachment attach = obtainGeneratedFile();
		if(attach!=null){
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
			bean.remove(attach);
		}
		Certifica2Batch batch = (Certifica2Batch) this.getTo();
		if(batch!=null){
			batch.setOutcomeFile(null);
			batch.setOutcomeFileDate(null);
			batch.setStatus(FileStatus.PENDING);
			this.getManagerBean().update(batch);
		}
	}

	
	public void changeBatchStatus(FileStatus status) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		Certifica2BatchAttachment attach = obtainGeneratedFile();
		setRecorded(attach!=null);
	}
	
	private Certifica2BatchAttachment obtainGeneratedFile() throws ManagerBeanException {
		Certifica2Batch batch = (Certifica2Batch)this.getTo();
		IManagerBean bean = BeanManager.getManagerBean(SepeBatchAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_SOURCE_BATCH), batch.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_ATTACHMENT_TYPE), SepeBatchAttachmentType.GENERATED_FILE);
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_DOMAIN));
		if(bean.getCount(criteria)>0){
			return (Certifica2BatchAttachment) bean.getList(criteria).get(0);
		} 
		return null;
	}
	
	public void onInitCertificados(ActionEvent event){
		Certifica2Batch batch =  (Certifica2Batch) this.getTo();
		CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
		certificadosController.initialize(batch);
	}
	
	/*
	 * INNER CLASSES
	 */
	public static class Certifica2BatchNewWizard implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Certifica2BatchController controller;
		
		private List<Certifica2BatchDetail> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		public Certifica2BatchNewWizard(Certifica2BatchController controller) {
			this.controller = controller;
		}

		public DataModel getSelectedModel() {
			return selectedModel;
		}

		public void setSelectedModel(DataModel selectedModel) {
			this.selectedModel = selectedModel;
		}

		public void init() {
			Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
			try {
				listController.clearCriteria();
			} catch (ManagerBeanException e) {
				// nada
			}
			listController.onEditSearch(null);
			listController.init(null);
			listController.setEndDateFrom(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), CommonUtil.getDay(new Date())-10));
			listController.setModel(null);
			
			selectedList = new LinkedList<Certifica2BatchDetail>();
			setSelectedModel(null);
		}
		
		private void saveData() {
			
			Certifica2BatchController batchController = (Certifica2BatchController) FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_CONTROLLER_NAME);
			
			Enterprise enterprise = selectedList.get(0).getContract().getWorkPlace().getEnterprise();
			Certifica2Batch batch = (Certifica2Batch) batchController.getTo();
			batch.setEnterprise(enterprise);
			batch.setDomain(enterprise.getDomain());
			batch.setDate(new Date());
			batch.setStatus(FileStatus.PENDING);
			batchController.accept(null);
			
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				IManagerBean detailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
				IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
				for(Certifica2BatchDetail detail: selectedList){
					detail.setCertifica2Batch(batch);
					detail.setDomain(batch.getDomain());
					detail.setStatus(FileStatus.PENDING);
					detailBean.insert(detail);
					contractBean.update(detail.getContract());
				}
				
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				AonUtil.addErrorMessage(e.getMessage());
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					throw new AbortProcessingException(msg  + daoe.getMessage());
				}
				String msg = "Error durante la grabación de datos. ";
				throw new AbortProcessingException(msg  + e.getMessage());
			} finally {
				HibernateUtil.closeSession(sessionName);
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}

		public void accept(ActionEvent event){
			if(selectedList==null || selectedList.size()<=0){
				AonUtil.addErrorMessage("Seleccione los contratos para continuar");
				throw new AbortProcessingException("Seleccione los contratos para continuar");
			}
			saveData();
			controller.loadDetails();
		}
		
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
			Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
	        listController.checkAllSuspensionCauses();
			
			List<Contract> duplicatedList = new LinkedList<Contract>();
			List<Integer> includedPersons = new LinkedList<Integer>();
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	Contract contract = (Contract) iterator.next();
	        	Certifica2BatchDetail detail = (Certifica2BatchDetail) listController.getBatchDetailList().get(contract.getId());
				if(includedPersons.contains(contract.getPerson().getId())){
					duplicatedList.add(contract);
				} else {
					detail.setContract(contract);
					detail.setStatus(FileStatus.PENDING);
					selectedList.add(detail);
					includedPersons.add(contract.getPerson().getId());
				}
			}
			if(duplicatedList.size()>0){
				AonUtil.addErrorMessage("Se han encontrado personas duplicadas. No se incluyen en la remesa.");
				for(Contract contract: duplicatedList){
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					String period = "(" + formatter.format(contract.getStartDate());
					period += " - ";
					period += formatter.format(contract.getEndDate()) + ")";
					AonUtil.addErrorMessage(contract.getPerson().getFullName() + " " + period);
				}
			}
			
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
		}

		public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
	        Iterator<Object> iterator = getCheckedList().iterator();
	        while(iterator.hasNext()){
	        	Certifica2BatchDetail detail = (Certifica2BatchDetail) iterator.next();
	        	if(selectedList.contains(detail)){
	        		selectedList.remove(detail);
	        	}
	        }
	        clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
	        controller.loadDetails();
	        controller.onSearchContracts(event);
		}
		
		public void rowSelected(ValueChangeEvent event) {
			if (event.getNewValue() != null) {
				setRowChecked(((Boolean) event.getNewValue()).booleanValue());
			}
		}

		public boolean getRowChecked() {
			if(getSelectedModel().isRowAvailable()){
				return checks.contains(getSelectedModel().getRowData());
			}
			return false;
		}

		public void setRowChecked(boolean rowChecked) {
			if (rowChecked) {
				if (!checks.contains(getSelectedModel().getRowData())) {
					checks.add(getSelectedModel().getRowData());
				}
			} else {
				if (checks.contains(getSelectedModel().getRowData())) {
					checks.remove(getSelectedModel().getRowData());
				}
			}
		}

		public ArrayList<Object> getCheckedList() {
			return checks;
		}

		public int getCheckedCount() {
			return checks!=null?checks.size():0;
		}

		public void clearCheckedList() {
			checks = new ArrayList<Object>();
		}

		public void checkAll(ActionEvent event) throws ManagerBeanException {
			Iterator<Certifica2BatchDetail> iterator = selectedList.iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (!checks.contains(o)) {
					checks.add(o);
				}
			}
		}

		public void checkNone(ActionEvent event) {
			clearCheckedList();
		}
		
		public boolean isRowDisabled(){
			BasicController controller = (BasicController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
			try {
				if(controller.getModel().isRowAvailable() && getSelectedModel()!=null){
					return isRowDisabled((Contract) controller.getModel().getRowData());
				}
			} catch (ManagerBeanException e) {
				// nada
			}
			return false;
		}
		
		public boolean isRowDisabled(Contract contract) throws ManagerBeanException{
			for(Certifica2BatchDetail detail: (List<Certifica2BatchDetail>)getSelectedModel().getWrappedData()){
				if(detail.getContract().getPerson().getId().equals(contract.getPerson().getId())){
					return true;
				}
			}
			return false;
		}
		
	}
	
}
