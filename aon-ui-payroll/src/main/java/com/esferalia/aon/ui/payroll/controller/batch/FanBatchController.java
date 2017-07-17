package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.servlet.ServletOutputStream;
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
import com.code.aon.common.enumeration.MimeType;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.file.format.output.FileOutput;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FANReportWriter;
import com.esferalia.aon.ui.payroll.file.FANWriter;
import com.esferalia.aon.ui.payroll.file.QuoteReportWriter;


public class FanBatchController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private FanBatchNewWizard newBatchWizard;
	
	public FanBatchNewWizard getNewBatchWizard() {
		if(newBatchWizard==null){
			newBatchWizard = new FanBatchNewWizard(this);
		}
		return newBatchWizard;
	}

	public void setNewBatchWizard(FanBatchNewWizard newBatchWizard) {
		this.newBatchWizard = newBatchWizard;
	}
	
	public boolean isRecorded() {
		return this.getTo()!=null && (((FanBatch)this.getTo()).getOutcomeFileSize()!=null && ((FanBatch)this.getTo()).getOutcomeFileSize()>0);
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean fanBatchDetailBean = BeanManager.getManagerBean(FanBatchDetail.class);
        FanListController listController = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			EnterpriseCCC ccc = (EnterpriseCCC) iterator.next();
            FanBatchDetail fanBatchDetail = new FanBatchDetail();
			fanBatchDetail.setCcc(ccc);
			fanBatchDetail.setFanBatch((FanBatch) getTo());
			fanBatchDetailBean.insert(fanBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean fanBatchDetailBean = BeanManager.getManagerBean(FanBatchDetail.class);
        BatchDetailController fanBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = fanBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	FanBatchDetail fanBatchDetail = (FanBatchDetail) iterator.next();
        	fanBatchDetailBean.remove(fanBatchDetail);
        }
		fanBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchCCCs(ActionEvent event) throws ManagerBeanException {
		FanListController list = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		FanListController list = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
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
			
			FanBatch batch = (FanBatch) getTo();
			FANWriter fanWriter = new FANWriter();
			FileOutput output = fanWriter.createFAN(getEnterpriseCCCList(),((FanBatch)getTo()).getLiquidationType(), batch.getYear(), batch.getMonth(), batch.getMonth());
			if (output != null && output.getContent() != null) {
				batch.setOutcomeFile(output.getContent());
				batch.setOutcomeFileDate(new Date());
				batch.setStatus(FileStatus.GENERATED);
				super.accept(null);
			}
			
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error generating FAN file");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onDownloadFile( ActionEvent event ) {
		FanBatch batch = (FanBatch) getTo();
		HttpServletResponse response = null;
		OutputStream out = null;
        try {
        	Date date = batch.getDate();
        	SimpleDateFormat formatter = new SimpleDateFormat("ddMMHHmm");
    		String name = formatter.format(date);
    		byte[] data = batch.getOutcomeFile();
        	int size = data.length;
			response = DownloadUtil.getResponse();
    		out = DownloadUtil.initDownload(response, name+".FAN", null, size);
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
		FanBatch batch = (FanBatch) this.getTo();
		if(batch!=null){
			batch.setOutcomeFile(null);
			batch.setOutcomeFileDate(null);
			batch.setStatus(FileStatus.PENDING);
			this.getManagerBean().update(batch);
		}
		onSearchCCCs(event);
	}
	
	public void fanFileUploaded(UploadEvent event) throws ManagerBeanException {
		FanBatch batch = (FanBatch) this.getTo();
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
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		List<EnterpriseCCC> list = new LinkedList<EnterpriseCCC>();
		for(ITransferObject to: controller.getWrappedList()){
			FanBatchDetail detail = (FanBatchDetail) to;
			list.add(detail.getCcc());
		}
		return list;
	}
	
	public String onExcelReport() {
		Connection conn = null; 
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
			FANReportWriter writer = new FANReportWriter();
			
			FanBatch batch = (FanBatch) getTo();
			batch.setStatus(FileStatus.PENDING);
			batch.setLiquidationType(LiquidationType.L00);
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = batch.getLiquidationType().getValue()+batch.getYear()+batch.getMonth().getName(locale);
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
			writer.buildFANReport(getEnterpriseCCCList(), batch.getLiquidationType(), batch.getYear(), batch.getMonth(), batch.getMonth());
			writer.excelReport(conn, locale, output);
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	public String onQuoteExcelReport() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		Connection conn = null; 
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
			QuoteReportWriter writer = new QuoteReportWriter();
			
			FanBatch batch = (FanBatch) getTo();
			batch.setStatus(FileStatus.PENDING);
			batch.setLiquidationType(LiquidationType.L00);
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = batch.getLiquidationType().getValue() + "_";
			fileName += batch.getMonth().getName(locale) + "_";
			fileName += batch.getYear();
			dateFormatter.applyPattern("yyyy/MM/dd_HH:mm:ss");
			fileName += " - " + dateFormatter.format(new Date());
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
			List<Integer> cccList = new LinkedList<Integer>();
			for(EnterpriseCCC ccc: this.getEnterpriseCCCList()){
				cccList.add(ccc.getId());
			}
			writer.excelReport(batch.getYear(), batch.getMonth(), null, cccList, output);
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	/*
	 * INNER CLASSES
	 */
	public static class FanBatchNewWizard implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private FanBatchController controller;

		private List<ITransferObject> selectedList;
		
		private ArrayList<Object> checks = new ArrayList<Object>();
		
		private DataModel selectedModel;

		private LiquidationType[] liquidationTypes;
				
		public FanBatchNewWizard(FanBatchController controller) {
			this.controller = controller;
		}

		public LiquidationType[] getLiquidationTypes() {
			return liquidationTypes;
		}

		public void setLiquidationTypes(LiquidationType[] liquidationTypes) {
			this.liquidationTypes = liquidationTypes;
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
			FanListController listController = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
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
			FanBatchController batchController = (FanBatchController) FormUtil.getController(IPayrollConstants.FAN_BATCH_CONTROLLER_NAME);
			FanBatch batch = (FanBatch) batchController.getTo();
			
			batchController.accept(null);
			
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				IManagerBean detailBean = BeanManager.getManagerBean(FanBatchDetail.class);
				for(ITransferObject to: selectedList){
					FanBatchDetail detail = (FanBatchDetail) to;
					detail.setFanBatch(batch);
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
	        FanListController listController = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
			Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
	        while (iterator.hasNext()) {
	        	EnterpriseCCC ccc = (EnterpriseCCC) iterator.next();
				FanBatchDetail detail = new FanBatchDetail();
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
	        	FanBatchDetail detail = (FanBatchDetail) iterator.next();
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
