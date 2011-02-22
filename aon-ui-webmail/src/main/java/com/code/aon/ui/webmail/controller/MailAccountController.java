package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.naming.Name;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;

public class MailAccountController extends LdapBasicController implements IWebMailConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailAccountController.class);
	
	private Name accountId;
	
	private boolean showMailAccountList;
	
	private List<SelectItem> mailAccounts;
	
	@Override
	public void updateBaseDN(Name parent) {
		String user = NameResolver.getFirstValue(parent);
		String domain = NameResolver.getValue(parent, 2);
		updateBaseDN(domain, user);
	}
	
	@Override
	protected void initDAO() {
		AuthPrincipal auth = Utils.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}

	private void updateBaseDN( String domain, String user )  {
		Name baseDN = NameResolver.getUserAccountsDN(domain, user);
		if ( getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
			getLdapDAO().setBaseDN( baseDN );	
		} else {
			baseDN = NameResolver.getDomainAccountsDN(domain);
			if ( getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().setBaseDN( baseDN );
			}
		}
	}	
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, MAIL_ACCOUNT_DUPLICATED, name);
	}		
	
	private void resetFolderController() {
		FolderController folderController = (FolderController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_FOLDER);
		if (folderController.getFolder()!=null){
			try {
				folderController.getFolder().getFolder().expunge();
				folderController.getFolder().getFolder().close(false);
				folderController.setFolder(null);
			} catch (MessagingException e) {
				LOGGER.error( e.getMessage(), e);
			}
		}		
	}
	
	private void changeMailAccount() {
		MailAccount previousAccount = null;
		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
		if ( webmail.isLogged() ) {
			webmail.getServer().disconnect();
			previousAccount = webmail.getServer().getAccount();			
		}
		try{
			webmail.init((MailAccount)super.getSelectedTO());
		} catch (Throwable e) {
			try {
				if ( previousAccount != null ) {
					webmail.init( previousAccount );	
				}
			} catch (MessagingException e1) {
				LOGGER.error( e.getMessage(), e);
			}
			AonUtil.addErrorMessage( e.getMessage() );
		} finally {
			if ( webmail.isLogged() ) {
				this.accountId = webmail.getServer().getAccount().getId();	
		    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_TREE);
		    	treeBean.initTree( webmail.getServer() );		
			} else {
				this.accountId = null;
			}
		}
	}
	
	public void onChangeServer(ActionEvent event) {
		resetFolderController();
		super.onSelect(event);
		changeMailAccount();
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

	public boolean isCurrentToActiveAccount() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			MailAccount ma = (MailAccount)getSelectedTO();
			return ObjectUtils.equals(accountId, ma.getId());
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
		updateCurrentMailAccount();
	}

	public void updateCurrentMailAccount() {
		if ( WebMailController.isConnectable() ) {
			WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
			if ( webmail.isLogged() ) {
				this.accountId = webmail.getServer().getAccount().getId();			
			} else {
				this.accountId = null;
			}
		}
	}
	
	public Name getAccountId() {
		return accountId;
	}

	public void setAccountId(Name accountId) {
		this.accountId = accountId;
	}

	public boolean isShowMailAccountList() {
		return showMailAccountList;
	}

	public void setShowMailAccountList(boolean showMailAccountList) {
		this.showMailAccountList = showMailAccountList;
	}
	
	public void onSelectMailAccount(ActionEvent event) {
		setShowMailAccountList(false);
	}

	public void onChangeMailAccount( ValueChangeEvent event ) {
		resetFolderController();
		Name newAccountId = (Name) event.getNewValue();
		for( int i = 0; i < this.mailAccounts.size(); i++ ) {
			if ( ObjectUtils.equals(newAccountId, this.mailAccounts.get(i).getValue()) ) {
				try {
					getModel().setRowIndex(i);
				} catch (ManagerBeanException e) {
					LOGGER.error( e.getMessage(), e);
				}
				break;
			}
		}
		super.onSelect(null);
		changeMailAccount();
		setShowMailAccountList(false);
	}
	
	public boolean isRemovable() {
		MailAccount account = (MailAccount)getTo();
		if ( account.isDefault() ) {
			return false;
		}
		if ( WebMailController.isConnectable() ) {
			WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
			if ( webmail.isLogged() ) {
				return ! this.accountId.equals(account.getId());
			}
		}
		return true;
	}
	
}
