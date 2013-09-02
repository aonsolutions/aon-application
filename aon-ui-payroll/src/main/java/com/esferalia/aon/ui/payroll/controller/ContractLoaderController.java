package com.esferalia.aon.ui.payroll.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.file.ContractAfiLoader;
import com.esferalia.aon.ui.payroll.file.ContractContrataLoader;
import com.esferalia.aon.ui.payroll.file.IContractLoader;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;

public class ContractLoaderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractLoaderController.class.getName());

	private AonFile aonFile;
	private StringWriter logString;
	private PrintWriter log;

	private boolean progressionPanelVisible;
	private boolean loadPressed;
	
	private IContractLoader loader;
	
	
	public IContractLoader getLoader() {
		return loader;
	}
	public void setLoader(IContractLoader loader) {
		this.loader = loader;
	}
	public boolean isProgressionPanelVisible() {
		return progressionPanelVisible;
	}
	public void setProgressionPanelVisible(boolean progressionPanelVisible) {
		this.progressionPanelVisible = progressionPanelVisible;
	}
	public boolean isLoadPressed() {
		return loadPressed;
	}
	public void setLoadPressed(boolean loadPressed) {
		this.loadPressed = loadPressed;
	}
	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
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
		try {
			initContractLoader();
		} catch (AonException e) {
			throw new AbortProcessingException(e.getMessage());
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onStart(ActionEvent event ) {
		setAonFile(null);
		setLoader(null);
		setLog( null );
		logString = null;
		
		setLoadPressed(false);
		setProgressionPanelVisible(false);
	}
	
	public void onLoad(ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionName);
		logString = new StringWriter( );
		setLog( new PrintWriter( logString ) );
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
			loader.checkMetadata(input);
			loader.validate(input);
			loader.load(input,session);
			input.close();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
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
	        setLoadPressed(false);
		}
	}
	
	public void onShowPanel(ActionEvent event) {
		setProgressionPanelVisible(true);
		setLoadPressed(true);
		LogPanelController logger = LogPanelController.getInstance();
		logger.reset();
	}
	
	public void onClosePanel(ActionEvent event) {
		setProgressionPanelVisible(false);
		setLoadPressed(false);		
	}
	
	public void onHelp(ActionEvent event ) {
		
	}

	public IContractLoader initContractLoader() throws AonException, IOException{
		if (getAonFile() == null || getAonFile().getData()==null) {
			throw new AbortProcessingException("La entrada está vacia!");
		}

		ByteArrayInputStream input = null;
		InputStreamReader inputReader = null;
		LineNumberReader reader = null;
		try {
			input = new ByteArrayInputStream(getAonFile().getData());
			inputReader = new InputStreamReader(input, SEPEFileUtils.XML_FILE_ENCODING);
			reader = new LineNumberReader(inputReader);
			loader = new ContractContrataLoader();
			if(loader.isValidFile(reader, input)){
				return loader;
			} 
			loader = new ContractAfiLoader();
			if(loader.isValidFile(reader, input)){
				return loader;
			}
			loader = null;
		} finally {
			reader.close();
			input.close();
		} 
		return null;
	}
	
}
