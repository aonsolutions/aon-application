package com.code.aon.ui.webmail.controller;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import javax.mail.Quota;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.bean.BundleConstants;

public class WebMailController implements IWebMailConstants, BundleConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebMailController.class);
	
	private AonServer server;
	
	private String initErrorMessage;
	
	private FolderController folderController;
	
	private List<String> rejectedExtensions;
	
	private int maxAttachmentSize;
	
	private boolean enableDragAndDrop;
	
	public WebMailController() {
		startWebmail();
	}

	private void startWebmail() {
		try {
			AuthPrincipal mailUser = AonUtil.getAuthPrincipal();
			if (mailUser != null) {
	    		initDefault(mailUser);
	    		initConfig(mailUser);
	    	}
		} catch (Throwable e) {
			LOGGER.error( "Error connecting to the Server", e);
			initErrorMessage = e.getMessage();	
		}
    }
	
	public boolean isLogged() {
		return (server != null) && server.isConnected();
	}

	public boolean isReady() {
		if ( server != null ) {
			try {
				server.ensureConnection();
				return true;
			} catch (MessagingException e) {
				LOGGER.error( "Error reconnecting to the Server", e);
				initErrorMessage = e.getMessage();	
			}			
		}
		return false;
	}
	
	public String getInitErrorMessage() {
		return initErrorMessage;
	}

	/**
	 * @return the server
	 */
	public AonServer getServer() {
		return server;
	}

	private void initDefault(AuthPrincipal user) throws ManagerBeanException, MessagingException {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		IMailAccount mailAccount = mailConfig.getDefaultMailAccount(false);
		if (mailAccount!=null) {
			init(mailAccount);
		}else{
			this.initErrorMessage = AonUtil.getMessage(BUNDLE_NAME, NOT_MAIL_ACCOUNT, user.getShortName());
		}
	}

	public void initBasic(IMailAccount mailAccount) throws MessagingException {
		server = new AonServer(mailAccount);
		server.connect();
		server.createBasicFolders();
	}
	
	public void init(IMailAccount mailAccount) throws MessagingException {
		initBasic(mailAccount);
		SpamController spamController = (SpamController) AonUtil.getRegisteredBean(BEAN_SPAM);
		spamController.updateSpamEnabled(mailAccount);
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(BEAN_TREE);
    	treeBean.initTree( getServer() );
	}

    public String getMillis() {
        return ""+new GregorianCalendar().getTimeInMillis();
    }

    public String getContext(){
    	return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    }

	public FolderController getFolderController() {
		if ( folderController == null ) {
	    	setFolderController( (FolderController)AonUtil.getRegisteredBean(BEAN_FOLDER) );
		}
		return folderController;
	}

	public void setFolderController(FolderController folderController) {
		this.folderController = folderController;
	}
	
	public double getQuotaPercent() {
		Quota.Resource quota = getServer().getQuotaResource();
		return ( quota.usage / (double) quota.limit );
	}
	
	public double getQuotaLimit() {
		return (getServer().getQuotaResource().limit / 1024.0);
	}
	
	public String getQuotaImage() {
		double percent = getQuotaPercent();
		String suffix = "90";
		if ( percent >= 1 ) {
			suffix = "exceeded";
		} else if ( percent < 0.20 ) {
			suffix = "20";
		} else if ( percent < 0.50 ) {
			suffix = "50";
		} else if ( percent < 0.70 ) {
			suffix = "70";
		}
		ResourceResolver resolver = (ResourceResolver) AonUtil.getRegisteredBean("aonResource");
		String filePath = "/images/quota-" + suffix + ".png";
		return resolver.getResolveLocal().get(filePath );
	}
	
	private void initConfig(AuthPrincipal principal) {
		this.maxAttachmentSize = -1;
		this.rejectedExtensions = Collections.emptyList();
		setEnableDragAndDrop(!AonUtil.isChrome());
	}

	public String isValidFile( AonFile file ) {
		String extension = StringUtils.lowerCase( FilenameUtils.getExtension(file.getFileName()) );
		if ( ! StringUtils.isEmpty(extension) && this.rejectedExtensions.contains(extension) ) {
			return "La extension del fichero " + file.getFileName() + " no esta permitida";
		}
		int size = (int) file.getFile().length();
		if ( (maxAttachmentSize != -1) && (size > maxAttachmentSize) ) {
			return "El fichero " + file.getFileName() + " supera el tamaño maximo permitido ("+ (maxAttachmentSize / 1024) +" Kb)";
		}
		return null;
	}
	
	public void poll( ActionEvent event ) {
		LOGGER.debug( "Connection ready: ", isReady() );
	}
	
    /**
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	if ( isLogged() ) {
    		getServer().disconnect();
    	}
    }

	public boolean isEnableDragAndDrop() {
		return enableDragAndDrop;
	}

	public void setEnableDragAndDrop(boolean enableDragAndDrop) {
		this.enableDragAndDrop = enableDragAndDrop;
	}	
    
}