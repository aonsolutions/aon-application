package com.code.aon.ui.webmail.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.bean.EmailFolder;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.code.aon.webmail.enumeration.MailAccountType;

public class MailConfigController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(MailConfigController.class);
	
	private ISignatureController signature;
	
	private IMailAccountController mailAccount;
	
	private IContactController contact;
	
	private boolean richTextEnabled = true;
	
	private boolean showMailAccountList;

	private TreeNode<EmailFolder> rootNode;
	
	private String selectedFolder;
	
	private IMailAccount currentAccount;
	
	private List<SelectItem> mailAccounts;
	
	private List<SelectItem> signatures;
	
	private List<SelectItem> connectionSecurities;
	
	private List<SelectItem> mailAccountTypes;
	
	private String mailAccountTitle;
	
	private String signatureTitle;
	
	private boolean skipDefaultAccountColumn;
	
	private boolean skipTypeColumn;
	
	private boolean showFolderWindow;
	
	public MailConfigController() {
		SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE_DB);
		setSignature(signature);
		MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT_DB);
		setMailAccount(account);			
		ContactDBController contact = (ContactDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_CONTACT_DB);
		setContact(contact);			
	}
	
	public IContactController getContact() {
		return contact;
	}

	public void setContact(IContactController contact) {
		this.contact = contact;
	}

	public ISignatureController getSignature() {
		return signature;
	}

	public void setSignature(ISignatureController signature) {
		this.signature = signature;
	}

	public IMailAccountController getMailAccount() {
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
	
	public boolean isMailAccountRemovable() {
		IMailAccount account = (IMailAccount) getMailAccount().getTo();
		if ( account.isEnterpriseAccount() ) {
			return true;
		}
		return true;
	}	
	
	public void onSetDefault(ActionEvent event) {
		try {
			IMailAccount currentAccount = getSelectMailAccount();
			updateMailAccountList();
			for( SelectItem item : getMailAccounts() ) {
				IMailAccount account = (IMailAccount) item.getValue();
				account.setDefaultAccount( ObjectUtils.equals(currentAccount, account) );
				getMailAccount().getManagerBean().update(account);			
			}
			getMailAccount().initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSetDefault exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);			
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
		EmailFolder folder = (EmailFolder) tree.getRowData();
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
			server.close();
		}
	}

	private void loadTree( AonServer server ) {
		Folder rootFolder = server.getRoot();
		EmailFolder folder = new EmailFolder(rootFolder, server);
		rootNode = new TreeNodeImpl<EmailFolder>();
		rootNode.setData(folder);
		addNodes(rootFolder, rootNode);
	}
	
	private void addNodes(Folder folder, TreeNode<EmailFolder> parent) {
		AonServer server = parent.getData().getServer();
		try {
			for( Folder childFolder : folder.list() ) {
				TreeNode<EmailFolder> node = new TreeNodeImpl<EmailFolder>();
				EmailFolder emailFoder = new EmailFolder(childFolder, server);
				node.setData(emailFoder);
				parent.addChild(emailFoder.getName(), node);
				if ( EmailFolder.isHoldFolders(childFolder) ) {
					addNodes(childFolder, node );
				}
			}
		} catch (MessagingException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}	
	
	public TreeNode<EmailFolder> getTreeNode() {
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

	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}
	
	public List<IMailAccount> getIMailAccounts(){
		Vector<IMailAccount> vector = new Vector<IMailAccount>();
		for( SelectItem item : getMailAccounts() ) {
			IMailAccount account = (IMailAccount) item.getValue();
			vector.add(account);
		}
		return vector;
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
	
	public int getMailAccountCount() {
		if ( mailAccounts == null ) {
			updateMailAccountList();
		}
		return mailAccounts.size();		
	}

	public void setMailAccounts(List<SelectItem> mailAccounts) {
		this.mailAccounts = mailAccounts;
	}

	public List<SelectItem> getSignatures() {
		return signatures;
	}

	public void updateSignatureList() {
		this.signatures = getSignature().getSignatures();
	}
	
	public IMailAccount getDefaultMailAccount( boolean skipConnectCheck ) {
		boolean connectDomainAccounts = skipConnectCheck;
		updateMailAccountList();
		IMailAccount defaultAccount = null;
		List<IMailAccount> list = new LinkedList<IMailAccount>();
		for( SelectItem item : this.mailAccounts ) {
			IMailAccount ma = (IMailAccount) item.getValue();
			if ( connectDomainAccounts || !ma.isEnterpriseAccount() ) {
				if ( ma.isDefaultAccount() ) {
					return ma;
				}
				list.add(ma);
			}
		}
		if ( !list.isEmpty() ) {
			defaultAccount = list.get(0);
		}
		return defaultAccount;
	}
	
	public void onInitMailAccount( ActionEvent event ) throws ManagerBeanException {
		setSkipDefaultAccountColumn(false);
		setSkipTypeColumn(true);
		setMailAccountTitle(null);
		if ( getMailAccount() instanceof MailAccountDBController ) {
			MailAccountDBController controller = (MailAccountDBController) getMailAccount();
			controller.updateUser(UserUtils.getInstance().getLoggedUser());
		}
		getMailAccount().onSearch(event);
	}

	public void onInitSignature( ActionEvent event ) throws ManagerBeanException {
		setSignatureTitle(null);
		if ( getSignature() instanceof SignatureDBController ) {
			SignatureDBController controller = (SignatureDBController) getSignature();
			controller.updateUser(UserUtils.getInstance().getLoggedUser());
		}
		getSignature().onSearch(event);
	}

	public void onInitContact( ActionEvent event ) throws ManagerBeanException {
		if ( getContact() instanceof ContactDBController ) {
			ContactDBController controller = (ContactDBController) getContact();
			controller.updateUser(UserUtils.getInstance().getLoggedUser());
		}
		getContact().onSearch(event);
	}

	public List<SelectItem> getConnectionSecurities() {
		if (connectionSecurities == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			connectionSecurities = new LinkedList<SelectItem>();
			for (ConnectionSecurity cs : ConnectionSecurity.values()) {
				String name = cs.getName(locale);
				SelectItem item = new SelectItem(cs, name);
				connectionSecurities.add(item);
			}
		}
		return connectionSecurities;
	}

	public String getMailAccountTitle() {
		if ( mailAccountTitle == null ) {
			return AonUtil.getMessage(ICommonMessages.MAIL_ACCOUNT_TITLE);
		}
		return mailAccountTitle;
	}

	public void setMailAccountTitle(String mailAccountTitle) {
		this.mailAccountTitle = mailAccountTitle;
	}

	public String getSignatureTitle() {
		if ( signatureTitle == null ) {
			return AonUtil.getMessage(ICommonMessages.SIGNATURE_TITLE);
		}
		return signatureTitle;
	}

	public void setSignatureTitle(String signatureTitle) {
		this.signatureTitle = signatureTitle;
	}

	public boolean isSkipDefaultAccountColumn() {
		return skipDefaultAccountColumn;
	}

	public void setSkipDefaultAccountColumn(boolean skipDefaultAccountColumn) {
		this.skipDefaultAccountColumn = skipDefaultAccountColumn;
	}	

	public boolean isShowFolderWindow() {
		return showFolderWindow;
	}

	public void setShowFolderWindow(boolean showFolderWindow) {
		this.showFolderWindow = showFolderWindow;
	}

	public boolean isSkipTypeColumn() {
		return skipTypeColumn;
	}

	public void setSkipTypeColumn(boolean skipTypeColumn) {
		this.skipTypeColumn = skipTypeColumn;
	}

	public List<SelectItem> getMailAccountTypes() {
		if (mailAccountTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			mailAccountTypes = new LinkedList<SelectItem>();
			for (MailAccountType type : MailAccountType.values()) {
				SelectItem item = new SelectItem(type, type.getName(locale));
				mailAccountTypes.add(item);			
			}
		}
		return mailAccountTypes;
	}

	public void setMailAccountTypes(List<SelectItem> mailAccountTypes) {
		this.mailAccountTypes = mailAccountTypes;
	}	
	
}