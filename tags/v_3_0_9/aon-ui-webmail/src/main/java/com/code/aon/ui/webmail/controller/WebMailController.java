package com.code.aon.ui.webmail.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonServer;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebMailController implements ILdapConstants {

	private static final Logger LOGGER = Logger.getLogger(WebMailController.class.getName());
	
	private static final String ORGANIZATION_NAME_ATTRIBUTE = "o";
	
	private AonServer server;
		
	private Entry aonUser;

	
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
		this.aonUser = getAonUser(Utils.getAuthPrincipal());
		createDefaultSignature(mailAccount);
	}
	
	public void initFull(MailAccount mailAccount) {
		initBasic(mailAccount);
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
    	treeBean.loadTree();
    	FolderController folderBean = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
    	folderBean.nodeSelected(getServer().getAonFolder(AonFolder.INBOX_FOLDER_NAME));
	}

    private MailAccount getAccount(AuthPrincipal mailUser) throws ManagerBeanException {
		IManagerBean beanAccount = AonUtil.getController(AonConstants.BEAN_MAIL_ACCOUNT).getManagerBean();
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME), MailAccount.DEFAULT_MAIL_ACCOUNT_NAME);
		Iterator<ITransferObject> iterAccount = beanAccount.getList(criteriaAccount).iterator();
		if (iterAccount.hasNext()){
			MailAccount mailAccount = (MailAccount)iterAccount.next();
			return mailAccount;
		}
		return null;
    }

    public String getCurrentDate() {
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        return formatter.format(new Date()).toUpperCase();
    }

    public String getMillis() {
        return ""+new GregorianCalendar().getTimeInMillis();
    }

    public String getContext(){
    	return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    }
    
	private Entry getAonUser( AuthPrincipal principal ) {
		Entry entry = null;
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(principal.getDomain(), principal.getShortName());
			String filter = LdapSession.getObjectClass("aonUser");
			entry = ldap.getLdapSession().get(dn.toString(), filter);
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting aonUser for " + principal + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return entry;
	}		

    private void createDefaultSignature(MailAccount mailAccount){
    	if ( mailAccount.getSignature() == null ) {
    		try {
				IManagerBean signatureBean = AonUtil.getController(AonConstants.BEAN_SIGNATURE).getManagerBean();
	        	String BASE_NAME = "com.code.aon.ui.webmail.i18n.messages";
	    		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	            ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
	        	Signature signature = new Signature();
	        	signature.setName(Utils.getAuthPrincipal().getDomain());
	        	signature.setSignature("<br><br><br><hr>"+
	        			"<b><font size='4'>"+ getLoggedUserName()+"</font></b><p>"+
	        			"<b><font size='2'>"+ getCompanyName()+"</font></b><p>"+
	        			"<br>"+
	        			"<i>"+bundle.getString("aon_webmail_signature_deftext")+"</i>");
				signatureBean.insert(signature);
				IManagerBean mailAccountBean = AonUtil.getController(AonConstants.BEAN_MAIL_ACCOUNT).getManagerBean();
				mailAccount.setSignature(signature);
				mailAccountBean.update(mailAccount);
    		} catch (ManagerBeanException e) {
    			LOGGER.severe( e.getMessage() );
        	}    		
    	}
    }
    
    public String getCompanyName(){
    	if (this.aonUser.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
    		return this.aonUser.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
    	}    	
    	return null;
    }

    public String getLoggedUserName() {
    	String userName = this.aonUser.getAsString(COMMON_NAME_ATTRIBUTE);
    	if (this.aonUser.containsKey(SURNAME_ATTRIBUTE) ) {
    		userName += " " + this.aonUser.getAsString(SURNAME_ATTRIBUTE);
    	}
        return userName;
    }

}
