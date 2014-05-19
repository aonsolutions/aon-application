package com.esferalia.aon.ui.payroll.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.esferalia.aon.ui.payroll.file.ContractAfiLoader;
import com.esferalia.aon.ui.payroll.file.ContractContrataLoader;
import com.esferalia.aon.ui.payroll.file.IContractLoader;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;

public class ContractLoaderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractLoaderController.class.getName());

	private AonFile aonFile;

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
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}
	
	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
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
		
		setLoadPressed(false);
		setProgressionPanelVisible(false);
	}
	
	public void onLoad(ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionName);
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
			String msg = "Error durante la carga de datos. ";
			logger.error(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
				logger.info("Se deshacen las inserciones realizadas.");
			} catch (DAOException daoe) {
				LOGGER.error("Unable to rollback transaction!", e);
			}
			LOGGER.error(msg, e);
			logger.finish();
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
		if (getAonFile() == null || (getAonFile().getSize()<=0)) {
			throw new AbortProcessingException("La entrada está vacia!");
		}

		InputStream input = null;
		InputStreamReader inputReader = null;
		LineNumberReader reader = null;
		try {
			input = getAonFile().openStream();
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
