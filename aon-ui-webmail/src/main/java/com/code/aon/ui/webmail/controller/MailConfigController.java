package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_WEBMAIL;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.CONNECT_DOMAIN_MAIL_ACCOUNTS_PROPERTY;

import java.util.LinkedList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class MailConfigController {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailConfigController.class);
	
	private ISignatureController signature;
	
	private IMailAccountController mailAccount;
	
	private boolean richTextEnabled = true;
	
	private boolean showMailAccountList;

	private boolean systemAccountEditable;

	private TreeNode<AonFolder> rootNode;
	
	private String selectedFolder;
	
	private IMailAccount currentAccount;
	
	private List<SelectItem> mailAccounts;
	
	private List<SelectItem> signatures;
	
	public MailConfigController() {
		if ( AonUtil.isSkipLdap() ) {
			SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(BEAN_SIGNATURE_DB);
			setSignature(signature);
			MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT_DB);
			setMailAccount(account);			
		}
	}
	
	public ISignatureController getSignature() {
		if ( signature == null ) {
			signature = (ISignatureController) AonUtil.getRegisteredBean(BEAN_SIGNATURE);
		}
		return signature;
	}

	public void setSignature(ISignatureController signature) {
		this.signature = signature;
	}

	public IMailAccountController getMailAccount() {
		if ( mailAccount == null ) {
			mailAccount = (IMailAccountController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT);
		}
		return mailAccount;
	}

	public Converter getMailAccountConverter() {
		return getMailAccount().getConverter();
	}		

	public Converter getSignatureConverter() {
		return getSignature().getConverter();
	}		
	
	public void setMailAccount(IMailAccountController mailAccount) {
		this.mailAccount = mailAccount;
	}
	
	public boolean isSystemAccountEditable() {
		return systemAccountEditable;
	}

	public void setSystemAccountEditable(boolean systemAccountEditable) {
		this.systemAccountEditable = systemAccountEditable;
	}
	
	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	
	private IMailAccount getSelectMailAccount() throws ManagerBeanException {
		if ( getMailAccount().getModel().isRowAvailable() ) {
			return (IMailAccount) getMailAccount().getModel().getRowData();	
		}
		return null;
	}
	
	public boolean isCurrentMailAccountEditable() throws ManagerBeanException {
		IMailAccount account = getSelectMailAccount();
		if ( account != null ) {
			return isSystemAccountEditable() || (!getSelectMailAccount().isDefault());
		}
		return false;
	}
	
	public boolean isActiveMailAccount() throws ManagerBeanException {
		IMailAccount account = getSelectMailAccount();
		if ( account != null ) {
			WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
			if ( webmail.getServer() != null ) {
				return ObjectUtils.equals(webmail.getServer().getAccount(), account);	
			}			
		}
		return false;
	}
	
	public boolean isMailAccountEditable() {
		IMailAccount account = (IMailAccount) getMailAccount().getTo();
		if ( account.isDefault() ) {
			return isSystemAccountEditable();
		}
		return true;
	}
	
	public boolean isMailAccountRemovable() {
		IMailAccount account = (IMailAccount) getMailAccount().getTo();
		if ( account.isDefault() ) {
			return false;
		}
		if ( WebMailController.isConnectable() ) {
			WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
			if ( webmail.isLogged() ) {
				return ObjectUtils.equals(webmail.getServer().getAccount(), account);
			}
		}
		return true;
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void resetDefaults() throws ManagerBeanException {
		List<IMailAccount> list = (List) getMailAccount().getModel().getWrappedData();
		for (IMailAccount account : list) {
			account.setDefaultAccount(false);
			getMailAccount().getManagerBean().update(account);
		}
	}

	public void onSetDefault(ActionEvent event) {
		try {
			resetDefaults();
			IMailAccount account = getSelectMailAccount();
			account.setDefaultAccount(true);
			getMailAccount().getManagerBean().update(account);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSetDefault exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);			
		}
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
	
	public void onChangeServer(ActionEvent event) {
		resetFolderController();
		try {
			changeMailAccount( getSelectMailAccount() );
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e);
		}
	}

	private void changeMailAccount( IMailAccount account ) {
		IMailAccount previousAccount = null;
		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
		if ( webmail.isLogged() ) {
			webmail.getServer().disconnect();
			previousAccount = webmail.getServer().getAccount();			
		}
		try{
			webmail.init( account );
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
		    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_TREE);
		    	treeBean.initTree( webmail.getServer() );		
			}
		}
	}

	public String getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(String selectedFolder) {
		this.selectedFolder = selectedFolder;
	}	
	
	public void selectFolder(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		AonFolder folder = (AonFolder) tree.getRowData();
		try {
			BeanUtils.setProperty( getMailAccount().getTo(), selectedFolder, folder.getFullName() );
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	public void loadFolders() {
		IMailAccount account = (IMailAccount) getMailAccount().getTo();
		this.rootNode = null;
		AonServer server = new AonServer(account);
		try {
			server.connect();
			loadTree(server);
		} catch (MessagingException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			server.disconnect();
		}
	}

	private void loadTree( AonServer server ) {
		AonFolder folder = new AonFolder(server.getRoot(), server);
		rootNode = new TreeNodeImpl<AonFolder>();
		rootNode.setData(folder);
		addNodes(rootNode);
	}
	
	private void addNodes(TreeNode<AonFolder> parent) {
		AonFolder folder = parent.getData();
		try {
			for( AonFolder aonFolder : folder.getFolderList() ) {
				TreeNode<AonFolder> node = new TreeNodeImpl<AonFolder>();
				node.setData(aonFolder);
				parent.addChild(aonFolder.getName(), node);
				if ( aonFolder.isHoldFolders() ) {
					addNodes( node );
				}
			}
		} catch (WebmailException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}	
	
	public TreeNode<AonFolder> getTreeNode() {
		return rootNode;
	}

	public IMailAccount getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(IMailAccount currentAccount) {
		this.currentAccount = currentAccount;
	}	


	public boolean isShowMailAccountList() {
		return showMailAccountList;
	}

	public void setShowMailAccountList(boolean showMailAccountList) {
		this.showMailAccountList = showMailAccountList;
	}
	
	public void onSelectMailAccount(ActionEvent event) {
		WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
		this.currentAccount = webmail.getServer().getAccount();			
		setShowMailAccountList(true);
	}
	
	public void onChangeMailAccount( ValueChangeEvent event ) {
		resetFolderController();
		IMailAccount newAccount = (IMailAccount) event.getNewValue();
		changeMailAccount(newAccount);
		setShowMailAccountList(false);
	}

	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}

	public void updateMailAccountList() {
		this.mailAccounts = getMailAccount().getMailAccounts();
	}
	
	public boolean isShowAccountList() {
		if ( mailAccounts == null ) {
			updateMailAccountList();
		}
		return mailAccounts.size() > 1;
	}

	public List<SelectItem> getSignatures() {
		return signatures;
	}

	public void updateSignatureList() {
		this.signatures = getSignature().getSignatures();
	}
	
	public IMailAccount getDefaultMailAccount() {
		boolean connectDomainAccounts = AonUtil.isBeanValue(BEAN_WEBMAIL, CONNECT_DOMAIN_MAIL_ACCOUNTS_PROPERTY);
		updateMailAccountList();
		IMailAccount defaultAccount = null;
		List<IMailAccount> list = new LinkedList<IMailAccount>();
		for( SelectItem item : this.mailAccounts ) {
			IMailAccount ma = (IMailAccount) item.getValue();
			if ( connectDomainAccounts || (! ma.isEnterpriseAccount()) ) {
				if ( ma.isDefaultAccount() ) {
					return ma;
				} else if ( ma.isDefault() ) {
					defaultAccount = ma;
				}
				list.add(ma);
			}
		}
		if ( (defaultAccount == null) && (!list.isEmpty()) ) {
			defaultAccount = list.get(0);
		}
		return defaultAccount;
	}
	
}