package com.code.aon.ui.accounting.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.accounting.invoice.SupplierLoaderManager;
import com.code.aon.ui.util.AonUtil;

public class SupplierLoaderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierLoaderController.class.getName());

	private AonFile aonFile;
	
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	
	private PrintWriter log;

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public PrintWriter getLog() {
		return log;
	}
	public void setLog(PrintWriter log) {
		this.log = log;
	}
	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onStart(ActionEvent event ) {
		setAonFile(null);
		setScope(null);
		setSecurityLevel(null);
		setWorkPlace(null);
		setLog( null );
	}
	
	public void onLoad(ActionEvent event ) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			StringWriter sw = new StringWriter();
			setLog( new PrintWriter( sw ) );
			SecurityLevel sl = (AonUtil.getRoleManager().isConfidentiality()?getSecurityLevel():SecurityLevel.OFFICIAL);
			SupplierLoaderManager m = new SupplierLoaderManager(getScope(),sl,getWorkPlace(), getLog() );
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
			m.loadMetadata(input);
			input = new ByteArrayInputStream(getAonFile().getData());
			m.validateFormat(input);
			input = new ByteArrayInputStream(getAonFile().getData());
			m.load(input);
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error recuperando proveedores. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
}
