package com.code.aon.ui.loader.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

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

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;

public class AonLoaderController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonLoaderController.class.getName());

	private LoaderParams params;
	private AonFile aonFile;

	private boolean progressionPanelVisible;
	private boolean loadPressed;

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
	public LoaderParams getParams() {
		return params;
	}
	public void setParams(LoaderParams params) {
		this.params = params;
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
		setParams(new LoaderParams());
		
		setLoadPressed(false);
		setProgressionPanelVisible(false);
	}
	
	public void onLoad(ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
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
	        loader.setFactoryManager(null);
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
