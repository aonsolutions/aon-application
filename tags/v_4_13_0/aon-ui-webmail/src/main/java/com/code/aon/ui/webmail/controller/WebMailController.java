package com.code.aon.ui.webmail.controller;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.Quota;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.bean.BundleConstants;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebMailController implements WebMailConstants, BundleConstants {

	private static final Logger LOGGER = Logger.getLogger(WebMailController.class.getName());
	
	private AonServer server;
	
	private String initErrorMessage;
	
	private FolderController folderController;
	
	public WebMailController() {
		startWebmail();
	}

	private void startWebmail() {
		try {
			AuthPrincipal mailUser = Utils.getAuthPrincipal();
			if (mailUser != null) {
	    		initDefault(mailUser);
	    	}
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Error connecting to the Server", e);
			initErrorMessage = e.getMessage();	
		}
    }
	
	public boolean isLogged() {
		return (getServer() != null) && getServer().isConnected();
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
		MailAccount mailAccount = WebmailUtil.getDefaultAccount(user.getDomain(),user.getShortName());
		if (mailAccount!=null) {
			init(mailAccount);
		}else{
    		AonUtil.addErrorMessage("NOT VALID ACCOUNT");
		}
	}

	public void init(MailAccount mailAccount) throws MessagingException {
		server = new AonServer(mailAccount);
		server.connect();
		server.createBasicFolders();
		createDefaultSignature(mailAccount);
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
    
    private void createDefaultSignature(MailAccount mailAccount){
    	if ( mailAccount.getSignature() == null ) {
    		try {
    			Signature signature = null;
    			String name = Utils.getAuthPrincipal().getDomain();
				IManagerBean signatureBean = FormUtil.getController(BEAN_SIGNATURE).getManagerBean();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(signatureBean.getFieldName(IWebMailAlias.SIGNATURE_NAME), name);
				List<ITransferObject> list = signatureBean.getList(criteria);
				if ( list.size() == 1) {
					signature = (Signature) list.get(0);
				} else {
					LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(BEAN_LOGGED_USER);					
		    		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		            ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale); 
		        	signature = new Signature();
		        	signature.setName(name);
		        	signature.setSignature("<br><br><br><hr>"+
		        			"<b><font size='4'>"+ loggedUser.getLoggedUserName()+"</font></b><p>"+
		        			"<b><font size='2'>"+ loggedUser.getCompanyName()+"</font></b><p>"+
		        			"<br>"+
		        			"<i>"+bundle.getString("webmail_signature_deftext")+"</i>");
					signatureBean.insert(signature);					
				}
				IManagerBean mailAccountBean = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();
				mailAccount.setSignature(signature);
				mailAccountBean.update(mailAccount);
    		} catch (ManagerBeanException e) {
    			LOGGER.severe( e.getMessage() );
        	}    		
    	}
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
	
}