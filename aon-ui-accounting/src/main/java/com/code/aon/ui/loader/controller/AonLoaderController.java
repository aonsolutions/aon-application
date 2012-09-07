package com.code.aon.ui.loader.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
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

	private boolean progressionPanelVisible;

	public boolean isProgressionPanelVisible() {
		return progressionPanelVisible;
	}
	public void setProgressionPanelVisible(boolean progressionPanelVisible) {
		this.progressionPanelVisible = progressionPanelVisible;
	}
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
		
		setProgressionPanelVisible(false);
	}
	
	public void onLoad(ActionEvent event ) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		logString = new StringWriter( );
		setLog( new PrintWriter( logString ) );
		Loader loader = new Loader(params);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
			loader.loadMetadata(input);
			input = new ByteArrayInputStream(getAonFile().getData());
			loader.validate(input);
			input = new ByteArrayInputStream(getAonFile().getData());
			loader.load(input,session);
			HibernateUtil.commitTransaction(sessionName);
			AonUtil.addInfoMessage("Carga de datos finalizada!");
		} catch (Exception e) {
			LogPanelController logger = LogPanelController.getInstance();
			logger.finish();
			AonUtil.addErrorMessage(e.getMessage());
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
			setProgressionPanelVisible(false);
	        loader.setFactoryManager(null);
		}
	}
	
	public void onShowPanel(ActionEvent event) {
		setProgressionPanelVisible(true);
	}
	
	public void onClosePanel(ActionEvent event) {
		setProgressionPanelVisible(false);
	}
	
	public void onHelp(ActionEvent event ) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ectx = ctx.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
			response.setContentType( "text/html" );
			Loader loader = new Loader(params);
			loader.help(response.getWriter());
			ctx.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AbortProcessingException(e.getMessage());
		}
	}
}
