package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FDIWriter;


public class LeaveBatchController extends BasicController {
	
	private FDIWriter fdiWriter;
	private LeaveBatchNewWizard newBatchWizard;
	
	private FDIWriter getFDIWriter() {
		if (fdiWriter == null) {
			fdiWriter = new FDIWriter();
		}
		return fdiWriter;
	}
	
	public LeaveBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new LeaveBatchNewWizard();
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(LeaveBatchNewWizard newBatchWizard) {
		this.newBatchWizard = newBatchWizard;
	}
	
	public boolean isRecorded() {
		return this.getTo()!=null && ((LeaveBatch)this.getTo()).getOutcomeFile()!=null;
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
        LeaveListController listController = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			ContractLeaveDetail detail = (ContractLeaveDetail) iterator.next();
            detail.setStatus(ContractLeaveStatus.BATCHED);
            contractLeaveDetailBean.update(detail);
            LeaveBatchDetail leaveBatchDetail = new LeaveBatchDetail();
			leaveBatchDetail.setContractLeaveDetail(detail);
			leaveBatchDetail.setLeaveBatch((LeaveBatch) getTo());
			leaveBatchDetailBean.insert(leaveBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchLeaves(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
		IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
        BatchDetailController leaveBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = leaveBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	LeaveBatchDetail leaveBatchDetail = (LeaveBatchDetail) iterator.next();
        	leaveBatchDetail.getContractLeaveDetail().setStatus(ContractLeaveStatus.PENDING);
        	contractLeaveDetailBean.update(leaveBatchDetail.getContractLeaveDetail());
        	leaveBatchDetailBean.remove(leaveBatchDetail);
        }
		leaveBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchLeaves(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchLeaves(ActionEvent event) throws ManagerBeanException {
		LeaveListController leaveList = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		onEditSearchList(event);
		leaveList.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		LeaveListController listController = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		listController.onEditSearch(event);
		listController.init();
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchLeaves(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			if(this.isNew()){
				getNewBatchWizard().accept(event);
			}
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			LeaveBatch batch = (LeaveBatch) getTo();
			File file = getFDIWriter().createFDI(getLeaveDetailList(), loggedUser).getFile();
			if (file != null) {
				batch.setOutcomeFile(IOUtils.toByteArray(new FileInputStream(file)));
				batch.setOutcomeFileDate(new Date());
				batch.setStatus(FileStatus.GENERATED);
				super.accept(null);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		}
	}
	
	public void onDownloadFile( ActionEvent event ) {
		LeaveBatch batch = (LeaveBatch) getTo();
		HttpServletResponse response = null;
		OutputStream out = null;
        try {
        	Date date = batch.getDate();
        	SimpleDateFormat formatter = new SimpleDateFormat("ddMMHHmm");
    		String name = formatter.format(date);
        	int size = batch.getOutcomeFile().length;
			response = DownloadUtil.getResponse();
    		out = DownloadUtil.initDownload(response, name+".FDI", null, size);
        	InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(batch.getOutcomeFile()) );
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
		LeaveBatch batch = (LeaveBatch) this.getTo();
		if(batch!=null){
			batch.setOutcomeFile(null);
			batch.setOutcomeFileDate(null);
			batch.setStatus(FileStatus.PENDING);
			this.getManagerBean().update(batch);
		}
		onSearchLeaves(event);
	}
	
	public void fanFileUploaded(UploadEvent event) throws ManagerBeanException {
		LeaveBatch batch = (LeaveBatch) this.getTo();
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

	private List<ContractLeaveDetail> getLeaveDetailList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		List<ContractLeaveDetail> list = new LinkedList<ContractLeaveDetail>();
		for(ITransferObject to: controller.getWrappedList()){
			LeaveBatchDetail detail = (LeaveBatchDetail) to;
			list.add(detail.getContractLeaveDetail());
		}
		return list;
	}
	
	/*
	 * INNER CLASSES
	 */
	public class LeaveBatchNewWizard {

		private List<ITransferObject> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		public boolean isNew(){
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
			LeaveListController listController = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
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
			LeaveBatchController batchController = (LeaveBatchController) FormUtil.getController(IPayrollConstants.LEAVE_BATCH_CONTROLLER_NAME);
			LeaveBatch batch = (LeaveBatch) batchController.getTo();
			
			batchController.accept(null);
			
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				IManagerBean detailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
				for(ITransferObject to: selectedList){
					LeaveBatchDetail detail = (LeaveBatchDetail) to;
					detail.setLeaveBatch(batch);
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
				String msg = "No se ha seleccionado ningún parte IT";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			saveData();
			loadDetails();
		}
		
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
	        LeaveListController listController = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	ContractLeaveDetail leaveDetail = (ContractLeaveDetail) iterator.next();
				LeaveBatchDetail detail = new LeaveBatchDetail();
				detail.setContractLeaveDetail(leaveDetail);
				selectedList.add(detail);
			}
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new ListDataModel(selectedList));
	        onSearchLeaves(event);
		}

		public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
	        Iterator<Object> iterator = getCheckedList().iterator();
	        while(iterator.hasNext()){
	        	LeaveBatchDetail detail = (LeaveBatchDetail) iterator.next();
	        	if(selectedList.contains(detail)){
	        		selectedList.remove(detail);
	        	}
	        }
	        clearCheckedList();
	        setSelectedModel(new ListDataModel(selectedList));
	        loadDetails();
	        onSearchLeaves(event);
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
