package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import javax.faces.model.ListDataModel;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.file.CertificadosWriter;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class Certifica2BatchController extends BasicController {

	private CertificadosWriter certificadosWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	private Certifica2BatchNewWizard newBatchWizard;
	
	private List<Certifica2BatchDetail> excludeEmployeeList;
	private boolean showExcludeEmployeeWindow;
	
	public List<Certifica2BatchDetail> getExcludeEmployeeList() {
		if(excludeEmployeeList==null){
			excludeEmployeeList = new LinkedList<Certifica2BatchDetail>();
		}
		return excludeEmployeeList;
	}

	public void setExcludeEmployeeList( List<Certifica2BatchDetail> excludeEmployeeList) {
		this.excludeEmployeeList = excludeEmployeeList;
	}

	public boolean isShowExcludeEmployeeWindow() {
		return showExcludeEmployeeWindow;
	}

	public void setShowExcludeEmployeeWindow(boolean showExcludeEmployeeWindow) {
		this.showExcludeEmployeeWindow = showExcludeEmployeeWindow;
	}
	
	private CertificadosWriter getCertificadosWriter() {
		if (certificadosWriter == null) {
			certificadosWriter = new CertificadosWriter();
		}
		return certificadosWriter;
	}
	
	public Certifica2BatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new Certifica2BatchNewWizard();
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
		try {
			List<ITransferObject> detailList = getCertifica2DetailList((Certifica2Batch) this.getTo());
			checkEmployeeSalaries(detailList);
			if(!getExcludeEmployeeList().isEmpty()){
				setShowExcludeEmployeeWindow(true);
			} else {
				Certifica2Batch batch = (Certifica2Batch)getTo();
				File file = getCertificadosWriter().createFile(batch, detailList);
				
				validateCertificadosFile(file);
				
				IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
				if (file != null) {
					FileInputStream in = new FileInputStream(file);
					byte[] data = IOUtils.toByteArray(in);
					Certifica2BatchAttachment attach;
					attach = new Certifica2BatchAttachment();
					attach.setCertifica2Batch((Certifica2Batch) getTo());
					attach.setDomain(((Certifica2Batch) getTo()).getDomain());
					attach.setMimeType(MimeType.MIME_XML);
					attach.setDescription(getCertificadosWriter().getFileName());
					attach.setSize(null);
					attach.setAttachmentType(SepeBatchAttachmentType.GENERATED_FILE);
					attach.setScope(null);
					attach.setData(data);
					attach.setAttachDate(new Date());
					bean.insertOrUpdate(attach);
					setRecorded(true);
					changeBatchStatus(FileStatus.GENERATED);
					Certifica2BatchAttachController controller = (Certifica2BatchAttachController) FormUtil.getController("certifica2BatchAttach");
					controller.initializeModel();
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		}
	}
	
	private void validateCertificadosFile(File file) {
		try {
			InputStream is = new FileInputStream(file);
			String schema = null;
			schema = SEPEFileUtils.CERTIFICADOS_SCHEMA_FILE_NAME;
			SEPEFileUtils.validateCertificadosXmlPattern(is, schema);
		} catch (SAXException saxe) {
			String msg = "Error de validación de Certific@2: ausencia de datos o formato no correcto";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(saxe.getMessage() );
		} catch (IOException ioe) {
			String msg = "Error de I/O al validar los datos";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(ioe.getMessage() );
		} catch (Exception e) {
			String msg = "Error general al validar los datos";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage() );
		}
	}

	private void checkEmployeeSalaries(List<ITransferObject> detailList) throws ManagerBeanException {
		excludeEmployeeList = null;
		SEPEUtils utils = SEPEUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		Criteria criteria = null;
		for(ITransferObject to: detailList){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), detail.getContract().getId());
			utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DOMAIN));
			if(bean.getCount(criteria)<=0){
				getExcludeEmployeeList().add(detail);
			}
		}
	}
	
	public void onContinueExcludingEmployees(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		for(Certifica2BatchDetail detail: getExcludeEmployeeList()){
			bean.remove(detail);
		}
		loadDetails();
		onCreateDisk(event);
	}

	public void changeBatchStatus(FileStatus status) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SepeBatchAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_SOURCE_BATCH), ((Certifica2Batch)this.getTo()).getId());
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_DOMAIN));
		if(bean.getCount(criteria)>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}
	
	private List<ITransferObject> getCertifica2DetailList(Certifica2Batch batch) {
		try {
			SEPEUtils utils = SEPEUtils.getInstance();
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
			utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_DOMAIN));
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.activity.type");
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.ccc");
			criteria.addOrder("Certifica2BatchDetail.contract.person.registry.document");
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se ha podido obtener la relación de empleados");
		}
	}
	
	
	public void onInitCertificados(ActionEvent event){
		Certifica2Batch batch =  (Certifica2Batch) this.getTo();
		if(batch.getStatus() == FileStatus.GENERATED){
			CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
			certificadosController.initialize(batch);
		}
	}
	
	/*
	 * INNER CLASSES
	 */
	public class Certifica2BatchNewWizard {

		private List<Certifica2BatchDetail> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
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
			loadDetails();
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
//					Certifica2BatchDetail detail = new Certifica2BatchDetail();
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
	        setSelectedModel(new ListDataModel(selectedList));
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
	        setSelectedModel(new ListDataModel(selectedList));
	        loadDetails();
	        onSearchContracts(event);
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
