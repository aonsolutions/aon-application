package com.code.aon.ui.webmail.controller;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonServer;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebMailController implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(WebMailController.class.getName());
	
	private AonServer server;
	
	private FolderController folderController;

	
	/**
	 * @return the server
	 */
	public AonServer getServer() {
		return server;
	}

	public void initDefault(AuthPrincipal mailUser){
		
		try {
			MailAccount mailAccount = getAccount(mailUser);
			if (mailAccount!=null) {
				initFull(mailAccount);
			}else{
	    		AonUtil.addErrorMessage("NOT VALID ACCOUNT");
			}
    	}catch (ManagerBeanException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}

	public void initDesktop(AuthPrincipal mailUser){
		try {
			MailAccount mailAccount = getAccount(mailUser);
			if (mailAccount!=null){
				initBasic(mailAccount);
			}else{
	    		AonUtil.addErrorMessage("NOT VALID ACCOUNT");
			}
    	}catch (ManagerBeanException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}

	public void initBasic(MailAccount mailAccount){
		server = new AonServer(mailAccount);
		server.createBasicFolders();
		createDefaultSignature(mailAccount);
		SpamController spamController = (SpamController) AonUtil.getRegisteredBean(BEAN_SPAM);
		spamController.updateSpamEnabled(mailAccount);
	}
	
	public void initFull(MailAccount mailAccount) {
		initBasic(mailAccount);
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(BEAN_TREE);
    	treeBean.initTree();
	}

    private MailAccount getAccount(AuthPrincipal mailUser) throws ManagerBeanException {
		IManagerBean beanAccount = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME), MailAccount.DEFAULT_MAIL_ACCOUNT_NAME);
		Iterator<ITransferObject> iterAccount = beanAccount.getList(criteriaAccount).iterator();
		if (iterAccount.hasNext()){
			MailAccount mailAccount = (MailAccount)iterAccount.next();
			return mailAccount;
		}
		return null;
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
		        			"<i>"+bundle.getString("aon_webmail_signature_deftext")+"</i>");
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
	
}
