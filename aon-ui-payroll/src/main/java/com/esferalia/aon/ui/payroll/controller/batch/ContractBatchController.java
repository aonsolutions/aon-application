package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.AFIWriter;


public class ContractBatchController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ContractBatchNewWizard newBatchWizard;
	
	public ContractBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new ContractBatchNewWizard(this);
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(ContractBatchNewWizard newBatchWizard) {
		this.newBatchWizard = newBatchWizard;
	}
	
	public boolean isRecorded() {
		return this.getTo()!=null && ((ContractBatch)this.getTo()).getOutcomeFileSize()!=null && ((ContractBatch)this.getTo()).getOutcomeFileSize()>0;
	}
	
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		IManagerBean contractBatchDetailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			ContractBatchDetail detail = (ContractBatchDetail) iterator.next();
            detail.getContract().setSsStatus(ContractStatus.PROCESSED);
            contractBean.update(detail.getContract());
            ContractBatchDetail contractBatchDetail = new ContractBatchDetail();
            contractBatchDetail.setActionType(detail.getActionType());
            contractBatchDetail.setLeaveType(detail.getLeaveType());
			contractBatchDetail.setContract(detail.getContract());
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
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			if(this.isNevv()){
				getNewBatchWizard().accept(event);
			}
			ContractBatch batch = (ContractBatch) getTo();
			AFIWriter afiWriter = new AFIWriter();
			
			LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
			List<ITransferObject> list = controller.getManagerBean().getList(controller.getCriteria());
			FileOutput output = afiWriter.createAFI(list.stream().map(to -> (ContractBatchDetail)to).collect(Collectors.toList()));
			
			if (output != null && output.getContent() != null) {
				batch.setOutcomeFile(output.getContent());
				batch.setOutcomeFileDate(new Date());
				batch.setStatus(FileStatus.GENERATED);
				super.accept(null);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error generating AFI file");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onDownloadFile( ActionEvent event ) {
		ContractBatch batch = (ContractBatch) getTo();
		HttpServletResponse response = null;
		OutputStream out = null;
        try {
        	Date date = batch.getDate();
        	SimpleDateFormat formatter = new SimpleDateFormat("ddMMHHmm");
    		String name = formatter.format(date);
    		byte[] data = batch.getOutcomeFile();
        	int size = data.length;
			response = DownloadUtil.getResponse();
    		out = DownloadUtil.initDownload(response, name+".AFI", null, size);
        	InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(data) );
        	IOUtils.copy( fileIn, out );
        	IOUtils.closeQuietly(fileIn);
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}
	
	public void onRemoveFile( ActionEvent event ) throws ManagerBeanException {
		ContractBatch batch = (ContractBatch) this.getTo();
		if(batch!=null){
			batch.setOutcomeFile(null);
			batch.setOutcomeFileDate(null);
			batch.setStatus(FileStatus.PENDING);
			this.getManagerBean().update(batch);
		}
		onSearchContracts(event);
	}
	
	public void fanFileUploaded(UploadEvent event) throws ManagerBeanException {
		ContractBatch batch = (ContractBatch) this.getTo();
		try {
			if(batch!=null){
				UploadItem item = event.getUploadItem();
				File file = item.getFile();
				if (file != null) {
					FileInputStream in = new FileInputStream(file);
					byte[] data = IOUtils.toByteArray(in);
					batch.setOutcomeFile(data);
					batch.setOutcomeFileDate(new Date());
					this.getManagerBean().update(batch);
				}
			}
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	private List<Contract> getContractList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
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
	public static class ContractBatchNewWizard implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ContractBatchController controller;

		private List<ITransferObject> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		public ContractBatchNewWizard(ContractBatchController controller) {
			this.controller = controller;
		}

		public boolean isNevv(){
			return true;
		}
		
		public DataModel getSelectedModel() {
			return selectedModel;
		}

		public void setSelectedModel(DataModel selectedModel) {
			this.selectedModel = selectedModel;
		}
		
		public List<ITransferObject> getSelectedList() {
			return selectedList;
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
			
			selectedList = new LinkedList<ITransferObject>();
			setSelectedModel(null);
		}
		
		private void saveData() {
			ContractBatchController batchController = (ContractBatchController) FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_CONTROLLER_NAME);
			ContractBatch batch = (ContractBatch) batchController.getTo();
			
			batchController.accept(null);
			
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				IManagerBean detailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
				IManagerBean infoBean = BeanManager.getManagerBean(ContractInfo.class);
				for(ITransferObject to: selectedList){
					ContractBatchDetail detail = (ContractBatchDetail) to;
					detail.setContractBatch(batch);
					detail.setStatus(FileStatus.PENDING);
					detailBean.insert(detail);
					
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

		public void accept(ActionEvent event) throws ManagerBeanException{
			onBatchSelected(event);
			if(selectedList==null || selectedList.size()<=0){
				String msg = "No se ha seleccionado ningún contrato";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			saveData();
			controller.loadDetails();
		}
		
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
	        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	ContractBatchDetail detail = (ContractBatchDetail) iterator.next();
				ContractBatchDetail newDetail = new ContractBatchDetail();
				newDetail.setActionType(detail.getActionType());
				newDetail.setLeaveType(detail.getLeaveType());
				newDetail.setContract(detail.getContract());
				selectedList.add(newDetail);
			}
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
	        controller.onSearchContracts(event);
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
			Iterator<ITransferObject> iterator = selectedList.iterator();
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
