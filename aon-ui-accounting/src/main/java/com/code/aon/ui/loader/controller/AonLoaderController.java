package com.code.aon.ui.loader.controller;

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
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.ThreadDomainProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.util.AonUtil;

public class AonLoaderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonLoaderController.class.getName());

	private LoaderParams params;
	private AonFile aonFile;
	private StringWriter logString;
	private PrintWriter log;
	private boolean enablePolling;

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	public LoaderParams getParams() {
		return params;
	}
	public void setParams(LoaderParams params) {
		this.params = params;
	}
	
	public PrintWriter getLog() {
		return log;
	}
	public void setLog(PrintWriter log) {
		this.log = log;
	}
	public String getLogString() {
		return logString==null?null:logString.toString();
	}
	
	public boolean isEnablePolling() {
        return enablePolling;
    }
    public void setEnablePolling(boolean enablePolling) {
        this.enablePolling = enablePolling;
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
			logString = null;
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onStart(ActionEvent event ) {
		setAonFile(null);
		setParams(new LoaderParams());
		setLog( null );
		logString = null;
	}
	
	public void onLoad(ActionEvent event ) {
		LoaderThread thread = new LoaderThread();
		thread.start();
	}
	
	private class LoaderThread extends Thread {
		
		private ThreadDomainProvider tdp;
		
		private LoaderThread() {
			tdp = new ThreadDomainProvider(
						this.getId(),
						DomainManager.getCurrentDomain(),
						DomainManager.isParentDomain(),
						DomainManager.isDomainManagementAvailable()
					);
			DomainManager.addDomainProvider( tdp );
		}
		
	    public void run() {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
			logString = new StringWriter( );
			setLog( new PrintWriter( logString ) );
			Loader loader = new Loader(params,log);
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
		        setEnablePolling( true );
				ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
				loader.loadMetadata(input);
				input = new ByteArrayInputStream(getAonFile().getData());
				loader.validateFormat(input);
				input = new ByteArrayInputStream(getAonFile().getData());
				loader.load(input);
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
					getLog().println("<br/>");
					getLog().println("Se deshacen las inserciones realizadas.");
					getLog().println("<br/>");
					getLog().println("<br/>");
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error durante la carga de datos. ";
				LOGGER.error(msg, e);
				getLog().println(msg);
				getLog().println("<br/>");
				getLog().println(e.getMessage());
				throw new AbortProcessingException(msg  + e.getMessage());
			} finally {
				HibernateUtil.closeSession(sessionName);
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
		        setEnablePolling( false );
		        DomainManager.removeDomainProvider( tdp );
		        loader.setFactoryManager(null);
			}
	    }

	}
}
