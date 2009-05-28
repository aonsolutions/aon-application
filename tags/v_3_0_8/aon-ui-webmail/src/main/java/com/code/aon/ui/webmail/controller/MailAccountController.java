package com.code.aon.ui.webmail.controller;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;

public class MailAccountController extends BasicController {

	private static final String MAIL_ACCOUNT_DUPLICATED = "aon_webmail_mailAccount_duplicated";

	private static final Logger LOGGER = Logger.getLogger(MailAccountController.class.getName());
	
	private String error;
	
	private LdapDAO dao;	
	
	private BasicManagerBean ldapManagerBean;

	public LdapDAO getDAO( AuthPrincipal principal ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		DistinguishedName baseDN = AonDN.getUserAccountsDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "MailAccount DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.dao = getDAO(Utils.getAuthPrincipal());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	private void addMessageExpression( String messageId ) {
		Locale locale = AonUtil.getCurrentLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(AonConstants.RESOURCE_BUNDLE, locale);
		addMessage( bundle.getString(messageId) );
	}
	
	@Override
	public void accept(ActionEvent event) {		
		String oldId = (String) this.savedToId;
		try {
			String currentId = this.dao.calculateDN(getTo());
			if ( isNew() ) {
				if ( dao.exists(currentId) ) {
					addMessageExpression(MAIL_ACCOUNT_DUPLICATED);
		            return;
				}			
			} else {
				if (! StringUtils.equals(oldId, currentId) ) {
					if ( dao.exists(currentId) ) {
						addMessageExpression(MAIL_ACCOUNT_DUPLICATED);
						return;
					}			
					getManagerBean().setId( getTo(), oldId );
					getManagerBean().remove( getTo() );
					getManagerBean().setId( getTo(), null );
					setNew(true);
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	public void onInit(ActionEvent event){
		error = null;
	}
	
	@SuppressWarnings("unused")
	public void onChangeServer(ActionEvent event){
		error = null;
		FolderController folderController = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
		if (folderController.getFolder()!=null){
			try {
				folderController.getFolder().getFolder().expunge();
				folderController.getFolder().getFolder().close(false);
				folderController.setFolder(null);
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
		}
		super.onSelect(event);
		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		webmail.getServer().disconnect();
		MailAccount previous = webmail.getServer().getAccount();
		try{
			webmail.initFull((MailAccount)super.getSelectedTO());
		}catch (Exception e) {
			error = e.getMessage();
			webmail.initFull((MailAccount)previous);
		}
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
    	treeBean.loadTree();
    	IController signatureController = (IController)AonUtil.getRegisteredBean(AonConstants.BEAN_SIGNATURE);
		signatureController.initializeModel();
		signatureController.onSearch(null);
    	BasicController emailController = (BasicController)AonUtil.getRegisteredBean(AonConstants.BEAN_CONTACT);
    	emailController.initializeModel();
    	emailController.onSearch(null);
		if (error==null){
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AonConstants.NAVIGATION_FOLDER);
		}
	}

	public boolean isToDefaultAccount(){
		MailAccount account = (MailAccount)getTo();
		return account.isDefault();
	}

	public boolean isCurrentToDefaultAccount(){
		MailAccount account = (MailAccount)getSelectedTO();
		return account.isDefault();
	}

}
