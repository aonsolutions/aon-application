package com.code.aon.ui.accounting.controller.entry;

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

import com.code.aon.accounting.Period;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.Scope;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.accounting.entry.XXAccountEntryLoaderManager;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryLoaderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryLoaderController.class.getName());

	private AonFile aonFile;
	
	private SecurityLevel securityLevel;
	private Period period;
	private Scope scope;
	private PrintWriter log;
	

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
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
		setSecurityLevel(null);
		setLog( null );
		setPeriod(null);
		setScope(null);
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
			XXAccountEntryLoaderManager m = new XXAccountEntryLoaderManager(getPeriod(),getScope(),sl, getLog() );
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
			m.validateFormat(input);
			input = new ByteArrayInputStream(getAonFile().getData());
			m.load(input);
			HibernateUtil.commitTransaction(sessionName);
			LOGGER.info("Carga de apuntes finalizada");
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error recuperando apunte [" + e.getMessage()+ "]";
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
