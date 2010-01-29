package com.code.aon.ui.webmail.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
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
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;

public class MailAccountController extends BasicController {

	private static final String MAIL_ACCOUNT_DUPLICATED = "aon_webmail_mailAccount_duplicated";

	private static final Logger LOGGER = Logger.getLogger(MailAccountController.class.getName());
	
	private LdapDAO dao;	
	
	private BasicManagerBean ldapManagerBean;
	
	private List<SelectItem> mailAccounts;

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
		ResourceBundle bundle = ResourceBundle.getBundle(WebMailConstants.RESOURCE_BUNDLE, locale);
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
	
	@SuppressWarnings("unused")
	public void onChangeServer(ActionEvent event){
		FolderController folderController = (FolderController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_FOLDER);
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
		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_WEBMAIL);
		webmail.getServer().disconnect();
		MailAccount previous = webmail.getServer().getAccount();
		try{
			webmail.initFull((MailAccount)super.getSelectedTO());
		}catch (Exception e) {
			webmail.initFull((MailAccount)previous);
			AonUtil.addErrorMessage( e.getMessage() );
		}
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(WebMailConstants.BEAN_TREE);
    	treeBean.initTree();
	}

	public boolean isToDefaultAccount(){
		MailAccount account = (MailAccount)getTo();
		return account.isDefault();
	}

	public boolean isCurrentToDefaultAccount() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			MailAccount account = (MailAccount)getSelectedTO();
			return account.isDefault();
		}
		return false;
	}

	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}

	@SuppressWarnings("unchecked")
	public void updateMailAccountList() throws ManagerBeanException {
		this.mailAccounts = new LinkedList<SelectItem>();
		Iterator iter = getManagerBean().getList(getCriteria()).iterator();
		while(iter.hasNext()){
			MailAccount mailAccount = (MailAccount)iter.next();
			SelectItem item = new SelectItem(mailAccount.getId(),mailAccount.getEmail());
			this.mailAccounts.add(item);
		}
	}
	
}
