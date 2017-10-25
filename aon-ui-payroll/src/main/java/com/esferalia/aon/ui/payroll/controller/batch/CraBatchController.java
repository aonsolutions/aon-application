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
import com.esferalia.aon.payroll.CraBatch;
import com.esferalia.aon.payroll.CraBatchDetail;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.CRAWriter;


public class CraBatchController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private CraBatchNewWizard newBatchWizard;
	
	public CraBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new CraBatchNewWizard(this);
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(CraBatchNewWizard newBatchWizard) {
		this.newBatchWizard = newBatchWizard;
	}
	
	public boolean isRecorded() {
		return this.getTo()!=null && ((CraBatch)this.getTo()).getOutcomeFileSize()!=null && ((CraBatch)this.getTo()).getOutcomeFileSize()>0;
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean craBatchDetailBean = BeanManager.getManagerBean(CraBatchDetail.class);
        CraListController listController = (CraListController) FormUtil.getController(IPayrollConstants.CRA_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			EnterpriseCCC ccc = (EnterpriseCCC) iterator.next();
            CraBatchDetail craBatchDetail = new CraBatchDetail();
			craBatchDetail.setCcc(ccc);
			craBatchDetail.setCraBatch((CraBatch) getTo());
			craBatchDetailBean.insert(craBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean craBatchDetailBean = BeanManager.getManagerBean(CraBatchDetail.class);
        BatchDetailController craBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CRA_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = craBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	CraBatchDetail craBatchDetail = (CraBatchDetail) iterator.next();
        	craBatchDetailBean.remove(craBatchDetail);
        }
		craBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CRA_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchCCCs(ActionEvent event) throws ManagerBeanException {
		CraListController list = (CraListController) FormUtil.getController(IPayrollConstants.CRA_LIST_CONTROLLER_NAME);
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		CraListController list = (CraListController) FormUtil.getController(IPayrollConstants.CRA_LIST_CONTROLLER_NAME);
		list.onEditSearch(event);
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchCCCs(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	public void onCreateDisk(ActionEvent event) {
		try {
			if(this.isNevv()){
				getNewBatchWizard().accept(event);
			}
			CraBatch batch = (CraBatch) getTo();
			CRAWriter craWriter = new CRAWriter();
			FileOutput output = craWriter.createCRA(getEnterpriseCCCList(), batch.getYear(), batch.getMonth());
			if(output.getErrors()!=null && output.getErrors().size()>0){
				AonUtil.addErrorMessage("Error generando el fichero CRA");
				output.getErrors().forEach(e -> AonUtil.addErrorMessage(e.getMessage()));
			} else if (output != null && output.getContent() != null) {
				batch.setOutcomeFile(output.getContent());
				batch.setOutcomeFileDate(new Date());
				batch.setStatus(FileStatus.GENERATED);
				super.accept(null);
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onDownloadFile( ActionEvent event ) {
		CraBatch batch = (CraBatch) getTo();
		HttpServletResponse response = null;
		OutputStream out = null;
        try {
        	Date date = batch.getDate();
        	SimpleDateFormat formatter = new SimpleDateFormat("ddMMHHmm");
    		String name = formatter.format(date);
    		byte[] data = batch.getOutcomeFile();
        	int size = data.length;
			response = DownloadUtil.getResponse();
    		out = DownloadUtil.initDownload(response, name+".CRA", null, size);
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
		CraBatch batch = (CraBatch) this.getTo();
		if(batch!=null){
			batch.setOutcomeFile(null);
			batch.setOutcomeFileDate(null);
			batch.setStatus(FileStatus.PENDING);
			this.getManagerBean().update(batch);
		}
		onSearchCCCs(event);
	}
	
	public void craFileUploaded(UploadEvent event) throws ManagerBeanException {
		CraBatch batch = (CraBatch) this.getTo();
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
	
	private List<EnterpriseCCC> getEnterpriseCCCList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CRA_BATCH_DETAIL_CONTROLLER_NAME);
		List<EnterpriseCCC> list = new LinkedList<EnterpriseCCC>();
		try {
			for(ITransferObject to: controller.getManagerBean().getList(controller.getCriteria())){
				CraBatchDetail detail = (CraBatchDetail) to;
				list.add(detail.getCcc());
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage());
		}
		return list;
	}
	
	/*
	 * INNER CLASSES
	 */
	public static class CraBatchNewWizard implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private List<ITransferObject> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;
		
		private CraBatchController controller;
		
		public CraBatchNewWizard(CraBatchController controller) {
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
			CraListController listController = (CraListController) FormUtil.getController(IPayrollConstants.CRA_LIST_CONTROLLER_NAME);
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
			CraBatchController batchController = (CraBatchController) FormUtil.getController(IPayrollConstants.CRA_BATCH_CONTROLLER_NAME);
			CraBatch batch = (CraBatch) batchController.getTo();
			
			batchController.accept(null);
			
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				IManagerBean detailBean = BeanManager.getManagerBean(CraBatchDetail.class);
				for(ITransferObject to: selectedList){
					CraBatchDetail detail = (CraBatchDetail) to;
					detail.setCraBatch(batch);
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
				String msg = "No se ha seleccionado ninguna cuenta de cotización.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			saveData();
			controller.loadDetails();
		}
		
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
	        CraListController listController = (CraListController) FormUtil.getController(IPayrollConstants.CRA_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	EnterpriseCCC ccc = (EnterpriseCCC) iterator.next();
				CraBatchDetail detail = new CraBatchDetail();
				detail.setCcc(ccc);
				selectedList.add(detail);
			}
	        listController.getCheckHandler().clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
	        controller.onSearchCCCs(event);
		}

		public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
	        Iterator<Object> iterator = getCheckedList().iterator();
	        while(iterator.hasNext()){
	        	CraBatchDetail detail = (CraBatchDetail) iterator.next();
	        	if(selectedList.contains(detail)){
	        		selectedList.remove(detail);
	        	}
	        }
	        clearCheckedList();
	        setSelectedModel(new SerializableListDataModel(selectedList));
	        controller.loadDetails();
	        controller.onSearchCCCs(event);
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
