package com.code.aon.ui.fiscal.controller;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.code.aon.fiscal.mod347.Mod347Parameters;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.file.MOD347Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class Mod347Controller extends BasicController {

	private FileOutput fileOutput;
	private FiscalParametersController fiscalParams;
	private Mod347Parameters params;
	
	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public Mod347Parameters getParams() {
		return params;
	}
	public void setParams(Mod347Parameters params) {
		this.params = params;
	}


	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	public void onCreateDisk(ActionEvent event) {
		MOD347Writer mod347Writer = new MOD347Writer();
		Mod347 mod347 = (Mod347) getTo();
		try {
			setFileOutput(mod347Writer.createMOD347(mod347));
		} catch (ManagerBeanException e) {
			String msg = "Error al generar el disco. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		if (getFileOutput() != null) {
			if (getFileOutput().getErrors().size() > 0) {
				AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY,
						IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
			}
		}
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			Mod347 mod347 = (Mod347) getTo();
			String fileName = "MOD347_" + mod347.getYear();
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	public void onFinish(ActionEvent event){
		Mod347 mod347 = (Mod347) getTo();
		mod347.setStatus(Mod347Status.FINISHED);
		accept(event);
	}
	public void onReopen(ActionEvent event){
		Mod347 mod347 = (Mod347) getTo();
		mod347.setStatus(Mod347Status.PENDING);
		accept(event);
	}
	
	@Override
	public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Mod347.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			super.accept(event);
			
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {

			}
			throw new AbortProcessingException(e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	/**
	 * Método NECESARIO para el listado.
	 */
	public int getYear() {
		return ((Mod347) getTo()).getYear();
	}
}
