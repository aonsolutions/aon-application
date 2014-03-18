package com.code.aon.ui.fiscal.controller.batch;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FiscalBatchController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<Batchable> pendingChecked;
	private List<Batchable> detailsChecked;
	private DataModel pending;
	private DataModel details;
	
	public DataModel getPending() {
		if (pending == null) {
			initializePendingModel();	
		}
		return pending;
	}
	public void setPending(DataModel pending) {
		this.pending = pending;
	}
	
	public DataModel getDetails() {
		if (details == null) {
			initializeDetailsModel();
		}
		return details;
	}
	public void setDetails(DataModel details) {
		this.details = details;
	}
	
	public List<Batchable> getPendingChecked() {
		if (pendingChecked == null) {
			initializePendingChecked();
		}
		return pendingChecked;
	}
	public void setPendingChecked(List<Batchable> pendingChecked) {
		this.pendingChecked = pendingChecked;
	}
	
	public List<Batchable> getDetailsChecked() {
		if (detailsChecked == null) {
			initializeDetailsChecked();
		}
		return detailsChecked;
	}
	public void setDetailsChecked(List<Batchable> detailsChecked) {
		this.detailsChecked = detailsChecked;
	}

	public void initialize() {
		initializePendingChecked();
		initializeDetailsChecked();
		initializePendingModel();
		initializeDetailsModel();
	}
	
	private void initializePendingChecked() {
		setPendingChecked(new ArrayList<Batchable>());
	}
	
	private void initializeDetailsChecked() {
		setDetailsChecked(new ArrayList<Batchable>());
	}

	private void initializePendingModel() {
		try {
			FiscalBatch fiscalBatch = (FiscalBatch) getTo();			
			List<Batchable> list = getBatchModel().getPendingList(fiscalBatch);
			setPending(new SerializableListDataModel(list));
		} catch (AonException e) {
			setPending(new SerializableListDataModel());
			String message = "Error mientras se recuperaban las declaraciones. (" + e.getMessage()+")";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
	private IFiscalBatchModel getBatchModel() throws AonException {
		FiscalBatch fiscalBatch = (FiscalBatch) getTo();
		FiscalBatchModelFactory factory = FiscalBatchModelFactory.getInstance();
		IFiscalBatchModel batchModel = factory.getFiscalBatchModel( fiscalBatch.getType() );
		return batchModel;
	}
	
	private void initializeDetailsModel() {
		try {
			FiscalBatchModelFactory factory = FiscalBatchModelFactory.getInstance();
			FiscalBatch fiscalBatch = (FiscalBatch) getTo();
			IFiscalBatchModel batchModel = factory.getFiscalBatchModel( fiscalBatch.getType() );
			List<Batchable> list = batchModel.getDetailsList(fiscalBatch);
			setDetails(new SerializableListDataModel(list));
		} catch (AonException e) {
			setDetails(new SerializableListDataModel());
			String message = "Error mientras se recuperaban las declaraciones. (" + e.getMessage()+")";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
	public int getPendingCheckedCount() {
		return getPendingChecked().size();
	}
	public boolean getPendingRowChecked() {
		Batchable batchable = (Batchable) getPending().getRowData();
		return getPendingChecked().contains(batchable);
	}
	public void setPendingRowChecked(boolean check) {
		Batchable batchable = (Batchable) getPending().getRowData();
		if (check) {
			getPendingChecked().add(batchable);
		} else {
			getPendingChecked().remove(batchable);
		}
	}
	
	public int getDetailsCheckedCount() {
		return getDetailsChecked().size();
	}
	public boolean getDetailsRowChecked() {
		Batchable batchable = (Batchable) getDetails().getRowData();
		return getDetailsChecked().contains(batchable);
	}
	public void setDetailsRowChecked(boolean check) {
		Batchable batchable = (Batchable) getDetails().getRowData();
		if (check) {
			getDetailsChecked().add(batchable);
		} else {
			getDetailsChecked().remove(batchable);
		}
	}

	@SuppressWarnings("unchecked")
	public void pendingCheckAll(ActionEvent event) {
		List<Batchable> list = (List<Batchable>) getPending().getWrappedData();
		for (Batchable b : list) {
			if (!getPendingChecked().contains(b)) {
				getPendingChecked().add(b);
			}
		}
	}
	public void pendingCheckNone(ActionEvent event) {
		initializePendingChecked();
	}
	
	@SuppressWarnings("unchecked")
	public void detailsCheckAll(ActionEvent event) {
		List<Batchable> list = (List<Batchable>) getDetails().getWrappedData();
		for (Batchable b : list) {
			if (!getDetailsChecked().contains(b)) {
				getDetailsChecked().add(b);
			}
		}
	}
	public void detailsCheckNone(ActionEvent event) {
		initializeDetailsChecked();
	}
	
	public void onBatchSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(FiscalBatchDetail.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
			FiscalBatch fb = (FiscalBatch) getTo();
			FiscalBatchDetail fbd = null;
			for (Batchable b :  getPendingChecked()) {
				fbd = new FiscalBatchDetail();
				fbd.setFiscalBatch(fb);
				fbd.setChildDomain(b.getDomain());
				fbd.setYear(fb.getYear());
				fbd.setPeriod(fb.getPeriod());
				fbd.setCompany(b.getCompany());
				fbd.setComplementary(b.isComplementary());
				fbd.setReplacement(b.isReplacement());
				fbd.setDescription(b.getDescription());
				fbd.setResult(b.getResult());
				fbd.setDetailId(b.getDetailId());
				bean.insert(fbd);
				getBatchModel().batch(fbd);
			}
			HibernateUtil.commitTransaction(sessionName);
			
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Error al añadir declaraciones del lote. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			initialize();
		}
	}
	public void onRemoveSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(FiscalBatchDetail.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
			for (Batchable b :  getDetailsChecked()) {
				FiscalBatchDetail detail = (FiscalBatchDetail) bean.get(b.getId());
				bean.remove(detail);
				getBatchModel().unbatch(detail);
			}
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Error al quitar declaraciones del lote. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			initialize();
		}
	}
	public void onClose(ActionEvent event) {
		FiscalBatch fb = (FiscalBatch) getTo();
		try {
			fb.setIssueDate( new Date() );
			byte[] data = getBatchModel().getData( fb );
			fb.setData( data );
			this.accept(event);
		} catch (AonException e) {
			String msg = "De momento no es posible realizar el archivo para la administración " + fb.getAdministration().getName(AonUtil.getCurrentLocale())+ ". " + e.getMessage();
			AonUtil.addInfoMessage(msg);
			throw new AbortProcessingException(msg);
		}		
	}
	public void onReopen(ActionEvent event) {
		FiscalBatch fb = (FiscalBatch) getTo();
		fb.setIssueDate( null );
		this.accept(event);
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FiscalBatch fb = (FiscalBatch) getTo();
			if (fb.getData() == null) {
				String msg = "El archivo no contiene datos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Lote_" + fb.getType() + "_" + fb.getYear() + "_" + fb.getPeriod();
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			ServletOutputStream output = response.getOutputStream();
			ByteArrayInputStream input = new ByteArrayInputStream( fb.getData() );
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();
			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			String msg = "Error al descargar el disco. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
}

 