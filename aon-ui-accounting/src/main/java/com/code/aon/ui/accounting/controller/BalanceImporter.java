package com.code.aon.ui.accounting.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.xml.sax.SAXException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.util.AonUtil;

public class BalanceImporter {
	private static final String RULES_FILE = "balance_import_digester.xml";
	private static Digester DIGESTER;
	private AonFile aonFile;

	private static final Digester getDigester() {
		if (DIGESTER == null) {
			DIGESTER = DigesterLoader.createDigester(BalanceImporter.class.getResource(RULES_FILE));
		}
		return DIGESTER;
	}

	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
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
			f.setFileName(item.getFileName());
			f.setMimeType( MimeType.get(item.getContentType()) );
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onInitialize(ActionEvent event) {
		setAonFile(null);
	}

	public void onImport(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			// BEGIN Transaction
			ByteArrayInputStream in = new ByteArrayInputStream(getAonFile().getData());
			parse(in);
			AonUtil.addInfoMessage("Proceso finalizado correctamente");
			// END Transaction
			
			//HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {

			}
			String msg = "Imposible realizar la importación de los balances";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
			
	}

	private void parse(InputStream in) throws ManagerBeanException {
		try {
			Digester digester = getDigester();
			digester.parse(in);
		} catch (IOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

}


