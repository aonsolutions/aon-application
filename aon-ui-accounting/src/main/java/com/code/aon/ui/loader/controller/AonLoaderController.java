package com.code.aon.ui.loader.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.custom.CustomLoaderFactoryManager;
import com.code.aon.ui.loader.custom.ICustomLoaderFactory;

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
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}
	public LoaderParams getParams() {
		return params;
	}
	public void setParams(LoaderParams params) {
		this.params = params;
	}
	
	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	public void onStart(ActionEvent event ) {
		onClear(event);
		
		loadCustomLoader();
	}
	
	public void onClear(ActionEvent event ) {
		setAonFile(null);
		setParams(new LoaderParams());
		
		setLoadPressed(false);
		setProgressionPanelVisible(false);
		
	}
	
	public void onLoad(ActionEvent event ) {
		if(isCustomLoaderEnabled() && isOppidumLoaderEnabled()){
			ICustomLoaderFactory loader = CustomLoaderFactoryManager.getFactory(getAonFile());
			if(loader != null){
				loader.load(new ByteArrayInputStream(getAonFile().getData()));
			} else {
				LogPanelController logPanel = LogPanelController.getInstance();
	        	logPanel.error("No se reconoce el fichero.");
	        	logPanel.error("Proceso abortado.");
			}
		} else {
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
				byte[] data = getAonFile().getData();
				ByteArrayInputStream input = new ByteArrayInputStream(data);
				loader.loadMetadata(input);
				input = new ByteArrayInputStream(data);
				loader.validate(input);
				input = new ByteArrayInputStream(data);
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
	
	/*
	 * CUSTOM LOADER
	 */
	
	private CustomLoadType selectedCustomLoader;
	
	public CustomLoadType getSelectedCustomLoader() {
		return selectedCustomLoader;
	}
	public void setSelectedCustomLoader(CustomLoadType selectedCustomLoader) {
		this.selectedCustomLoader = selectedCustomLoader;
	}
	
	public List<SelectItem> getCustomLoaderTypes() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (CustomLoadType type : CustomLoadType.values()) {
			SelectItem item = new SelectItem(type, type.name());
			list.add(item);
		}
		return list;
	}
	
	private void loadCustomLoader(){
		selectedCustomLoader = null;
		ApplicationParameter param = AppParamUtil.getParameter("CUSTOM_LOADER");
		if(param != null){
			if(param.getValue().equals(CustomLoadType.OPPIDUM.name())){
				selectedCustomLoader = CustomLoadType.OPPIDUM;
			}
		}
	}
	
	public boolean isCustomLoaderEnabled(){
		ApplicationParameter param = AppParamUtil.getParameter("CUSTOM_LOADER");
		return param != null && param.getValue().equals(CustomLoadType.OPPIDUM.name());
	}
	
	public boolean isOppidumLoaderEnabled(){
		return getSelectedCustomLoader()==CustomLoadType.OPPIDUM;
//		ApplicationParameter param = AppParamUtil.getParameter("CUSTOM_LOADER");
//		return param != null && param.getValue().equals(CustomLoadType.OPPIDUM.name());
	}
	
	public void onEnableOppidum(ActionEvent event) {
		ApplicationParameter param = AppParamUtil.getParameter("CUSTOM_LOADER");
		if(param == null){
			param = new ApplicationParameter("CUSTOM_LOADER", "");
		}
		param.setValue(CustomLoadType.OPPIDUM.name());
		AppParamUtil.insertParameter(param);
		loadCustomLoader();
	}
	public void onDisableOppidum(ActionEvent event) {
		ApplicationParameter param = AppParamUtil.getParameter("CUSTOM_LOADER");
		if(param != null){
			param.setValue(null);
			AppParamUtil.insertParameter(param);
		}
		loadCustomLoader();
	}
	
	public enum CustomLoadType {
		OPPIDUM;
	}
}
