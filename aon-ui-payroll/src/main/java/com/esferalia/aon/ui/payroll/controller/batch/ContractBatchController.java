package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchAttachment;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.AFIWriter;


public class ContractBatchController extends BasicController {
	
	private AFIWriter afiWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	private ContractBatchNewWizard newBatchWizard;
	
	private AFIWriter getAFIWriter() {
		if (afiWriter == null) {
			afiWriter = new AFIWriter();
		}
		return afiWriter;
	}
	
	public ContractBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new ContractBatchNewWizard();
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(ContractBatchNewWizard newBatchWizard) {
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
        IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		IManagerBean contractBatchDetailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			Contract contract = (Contract) iterator.next();
            contract.setSsStatus(ContractStatus.PROCESSED);
            contractBean.update(contract);
            ContractBatchDetail contractBatchDetail = new ContractBatchDetail();
			contractBatchDetail.setContract(contract);
			contractBatchDetail.setContractBatch((ContractBatch) getTo());
			contractBatchDetail.setStatus(FileStatus.PENDING);
			contractBatchDetailBean.insert(contractBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contractBatchDetailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController contractBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = contractBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	ContractBatchDetail contractBatchDetail = (ContractBatchDetail) iterator.next();
        	contractBatchDetail.getContract().setSsStatus(ContractStatus.PENDING);
        	contractBean.update(contractBatchDetail.getContract());
        	contractBatchDetailBean.remove(contractBatchDetail);
        }
		contractBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) throws ManagerBeanException {
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
		onEditSearchList(event);
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
		listController.onEditSearch(event);
		listController.init();
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onInit(event);
	}
	
//	@Override
//	public void onAccept(ActionEvent event) {
//		ContractBatch b = (ContractBatch) getTo();
//		b.setStatus(FileStatus.PENDING);
//		super.onAccept(event);
//		onSearchContracts(event);
//	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		ContractBatch b = (ContractBatch) getTo();
		b.setStatus(FileStatus.PENDING);
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			File file = getAFIWriter().createAFI(getContractList()).getFile();
			IManagerBean bean = BeanManager.getManagerBean(ContractBatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				ContractBatchAttachment attach;
				attach = new ContractBatchAttachment();
				attach.setContractBatch((ContractBatch) getTo());
				attach.setMimeType(MimeType.MIME_TXT);
				attach.setDescription(getAFIWriter().getEti().getFichero());
				attach.setSize(null);
				attach.setAttachmentType(PayrollBatchAttachmentType.GENERATED_DOCUMENT);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				ContractBatchAttachController controller = (ContractBatchAttachController) FormUtil.getController("contractBatchAttach");
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		ContractBatch b = (ContractBatch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_ATTACH_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}

	private List<Contract> getContractList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
		try {
			controller.getCriteria().setSkipDomainFilter(DomainManager.isDomainManagementAvailable());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Contract> list = new LinkedList<Contract>();
		for(ITransferObject to: controller.getWrappedList()){
			ContractBatchDetail detail = (ContractBatchDetail) to;
			list.add(detail.getContract());
		}
		return list;
	}
	
	/*
	 * INNER CLASSES
	 */
	public class ContractBatchNewWizard {

		private List<ContractBatchDetail> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		public DataModel getSelectedModel() {
			return selectedModel;
		}

		public void setSelectedModel(DataModel selectedModel) {
			this.selectedModel = selectedModel;
		}

		public void init() {
			ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
			try {
				listController.clearCriteria();
			} catch (ManagerBeanException e) {
				// nada
			}
			listController.onEditSearch(null);
			listController.init();
			listController.setModel(null);
			
			selectedList = new LinkedList<ContractBatchDetail>();
			setSelectedModel(null);
		}
		
		private void saveData() {
			ContractBatchController batchController = (ContractBatchController) FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_CONTROLLER_NAME);
			ContractBatch batch = (ContractBatch) batchController.getTo();
			
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
				
				IManagerBean detailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
				IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
				for(ContractBatchDetail detail: selectedList){
					detail.setContractBatch(batch);
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
			loadDetails();
		}
		
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
	        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	Contract contract = (Contract) iterator.next();
				ContractBatchDetail detail = new ContractBatchDetail();
				detail.setContract(contract);
				selectedList.add(detail);
			}
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new ListDataModel(selectedList));
		}

		public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
	        Iterator<Object> iterator = getCheckedList().iterator();
	        while(iterator.hasNext()){
	        	ContractBatchDetail detail = (ContractBatchDetail) iterator.next();
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
			Iterator<ContractBatchDetail> iterator = selectedList.iterator();
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
