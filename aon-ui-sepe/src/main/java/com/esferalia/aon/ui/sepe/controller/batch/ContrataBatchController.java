package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.ContrataBatchAttachment;
import com.esferalia.aon.payroll.ContrataBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataBatchController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContrataBatchController.class);
	
	private FileOutput fileOutput;
	private boolean recorded;
	private ContrataBatchNewWizard newBatchWizard;
	
	public ContrataBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new ContrataBatchNewWizard(this);
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(ContrataBatchNewWizard newBatchWizard) {
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
	
	public String getRowContractCode(){
		try {
			BatchDetailController detailController = (BatchDetailController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
			if(detailController.getModel().isRowAvailable()){
				ContrataBatchDetail detail = (ContrataBatchDetail) detailController.getModel().getRowData();
				if(detail!=null){
					String code = SEPEUtils.getInstance().getDataCurrentValue(detail.getContract(), ContextVariable.TC2.getName());
					ContractCode contractCode = ContractCode.getContractCodeByValue(code);
					return contractCode!=null?code + " - " + contractCode.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()):"";
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
			if(!isRecorded()){
				ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
				list.onSearch(event);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		ContrataBatch batch = (ContrataBatch) getTo();
		if(batch != null){
			batch.setStatus(status);
			super.accept(null);
		}
	}
	
	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
		listController.onEditSearch(event);
		listController.init();
	}
	
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contrataBatchDetailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
		ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			Contract contract = (Contract) iterator.next();
            ContrataBatchDetail contrataBatchDetail = new ContrataBatchDetail();
			contrataBatchDetail.setContract(contract);
			contrataBatchDetail.setContrataBatch((ContrataBatch) getTo());
			contrataBatchDetail.setStatus(FileStatus.PENDING);
			contrataBatchDetailBean.insert(contrataBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contrataBatchDetailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
        BatchDetailController contrataBatchDetailController = (BatchDetailController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = contrataBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	ContrataBatchDetail contrataBatchDetail = (ContrataBatchDetail) iterator.next();
        	contrataBatchDetailBean.remove(contrataBatchDetail);
        }
		contrataBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) {
		ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
		listController.init();
		listController.onSearch(event);
	}
	
	public void onCreateDisk(ActionEvent event) {
		try {
			byte[] data = createData();
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatchAttachment.class);
			if (data != null) {
				ContrataBatchAttachment attach;
				attach = new ContrataBatchAttachment();
				attach.setContrataBatch((ContrataBatch) getTo());
				attach.setMimeType(MimeType.MIME_XML);
				attach.setDescription(getFileDescription());
				attach.setSize(null);
				attach.setAttachmentType(SepeBatchAttachmentType.GENERATED_FILE);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				ContrataBatchAttachController controller = (ContrataBatchAttachController) FormUtil.getController(ISepeConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateContrataFile ["+e.getMessage()+"]");
		}
	}
	
	private String getFileDescription() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new Date());
	}

	private byte[] createData() {
		
		ContrataBatch batch = (ContrataBatch) this.getTo();
		
		String XML_DEF_TAG = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
		String XML_OPEN_TAG = null;
		String XML_END_TAG = null;
		if(batch.getType()==ContrataFileType.CONTRACT){
			XML_OPEN_TAG = "<CONTRATOS>";
			XML_END_TAG = "</CONTRATOS>";
		} else if(batch.getType()==ContrataFileType.EXTENSION){
			XML_OPEN_TAG = "<PRORROGAS>";
			XML_END_TAG = "</PRORROGAS>";
		} else if(batch.getType()==ContrataFileType.TRANSFORMATION){
			XML_OPEN_TAG = "<TRANSFORMACIONES>";
			XML_END_TAG = "</TRANSFORMACIONES>";
		}
		String XML_NEW_LINE = "\n";
		
		int offset = XML_DEF_TAG.length() + XML_NEW_LINE.length() + XML_OPEN_TAG.length() + XML_NEW_LINE.length();
		int lenght_increase = offset + XML_END_TAG.length() + XML_NEW_LINE.length();
		
		try {
			LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
			List<Integer> contractIds = new LinkedList<Integer>();
			for(ITransferObject to: batchDetailController.getWrappedList()){
				contractIds.add(((ContrataBatchDetail)to).getContract().getId());
			}
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contractIds);
			if(batch.getType()==ContrataFileType.CONTRACT){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_CONTRACT_FILE);
			} else if(batch.getType()==ContrataFileType.EXTENSION){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_EXTENSION_FILE);
			} else if(batch.getType()==ContrataFileType.TRANSFORMATION){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_TRANSFORM_FILE);
			}
			criteria.setSkipDomainFilter(true);
			List<ITransferObject> list = bean.getList(criteria);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(XML_DEF_TAG.getBytes());
			os.write(XML_NEW_LINE.getBytes());
			os.write(XML_OPEN_TAG.getBytes());
			os.write(XML_NEW_LINE.getBytes());
			for(ITransferObject to: list){
				ContractAttachment attach = (ContractAttachment) to;
				byte[] data = attach.getData();
				os.write(data, offset, data.length-lenght_increase);
			}
			os.write(XML_END_TAG.getBytes());
			return os.toByteArray();
		} catch (ManagerBeanException e) {
			String msg = "El fichero no se ha podido generar.";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "El fichero no se ha podido generar.";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	/*
	 * INNER CLASSES
	 */
	public static class ContrataBatchNewWizard implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ContrataBatchController controller;

		private List<ContrataBatchDetail> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		public ContrataBatchNewWizard(ContrataBatchController controller) {
			this.controller = controller;
		}

		public DataModel getSelectedModel() {
			return selectedModel;
		}

		public void setSelectedModel(DataModel selectedModel) {
			this.selectedModel = selectedModel;
		}

		public void init() {
			ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
			try {
				listController.clearCriteria();
			} catch (ManagerBeanException e) {
				// nada
			}
			listController.onEditSearch(null);
			listController.init();
			listController.setStartDateFrom(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), CommonUtil.getDay(new Date())-10));
			listController.setModel(null);
			
			selectedList = new LinkedList<ContrataBatchDetail>();
			setSelectedModel(null);
		}
		
		private void saveData() {
			ContrataBatchController batchController = (ContrataBatchController) FormUtil.getController(ISepeConstants.CONTRATA_BATCH_CONTROLLER_NAME);
			ContrataBatch batch = (ContrataBatch) batchController.getTo();
			
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
				
				IManagerBean detailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
				IManagerBean contratBean = BeanManager.getManagerBean(Contract.class);
				for(ContrataBatchDetail detail: selectedList){
					detail.setContrataBatch(batch);
					detail.setDomain(batch.getDomain());
					detail.setStatus(FileStatus.PENDING);
					detailBean.insert(detail);
					contratBean.update(detail.getContract());
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
				String msg = "Error durante el borrado de datos. ";
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
			ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	Contract contract = (Contract) iterator.next();
				ContrataBatchDetail detail = new ContrataBatchDetail();
				detail.setContract(contract);
				selectedList.add(detail);
			}
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
		}

		public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
	        Iterator<Object> iterator = getCheckedList().iterator();
	        while(iterator.hasNext()){
	        	ContrataBatchDetail detail = (ContrataBatchDetail) iterator.next();
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
			Iterator<ContrataBatchDetail> iterator = selectedList.iterator();
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
		
	}
	

}
