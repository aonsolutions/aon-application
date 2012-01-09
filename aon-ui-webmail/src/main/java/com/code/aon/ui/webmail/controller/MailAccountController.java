package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.NameResolver.DOMAINS;
import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.naming.Name;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.converter.LdapTransferObjectConverter;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class MailAccountController extends LdapBasicController implements IWebMailConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailAccountController.class);
	
	private Converter converter;
	
	private MailAccount currentAccount;
	
	private boolean showMailAccountList;
	
	private TreeNode<AonFolder> rootNode;
	
	private List<SelectItem> domainMailAccounts;
	
	private List<SelectItem> mailAccounts;
	
	private String selectedFolder;
	
	private boolean systemAccountEditable;
	
	@Override
	public boolean updateBaseDN(Name parent) {
		boolean updated = false;
		String container = NameResolver.getValue(parent, 1);
		if ( StringUtils.equals(container, DOMAINS) ) {
			String domain = NameResolver.getFirstValue(parent);
			updated = updateBaseDN( domain );
		} else {
			String user = NameResolver.getFirstValue(parent);
			String domain = NameResolver.getValue(parent, 2);
			updated = updateBaseDN(domain, user);			
		}
		return updated;
	}
	
	@Override
	protected void initDAO() {
		AuthPrincipal auth = Utils.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}

	private boolean updateBaseDN( String domain )  {
		Name domainDN = NameResolver.getDomainDN(domain);
		if ( getLdapDAO().exists(domainDN, DOMAIN) ) { 
			Name baseDN = NameResolver.getDomainAccountsDN(domain);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", domainDN );	
		return false;			
	}	
	
	private boolean updateBaseDN( String domain, String user )  {
		Name userDN = NameResolver.getUserDN(domain, user);
		if ( getLdapDAO().exists(userDN, USER) ) { 
			Name baseDN = NameResolver.getUserAccountsDN(domain, user);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", userDN );	
		return false;			
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
				setCurrentAccount( webmail.getServer().getAccount() );	
		    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_TREE);
		    	treeBean.initTree( webmail.getServer() );		
			} else {
				setCurrentAccount(null);
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

	public boolean isCurrentEditable() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			MailAccount account = (MailAccount)getSelectedTO();
			return isSystemAccountEditable() || (!account.isDefault());
		}
		return false;
	}

	public boolean isCurrentToActiveAccount() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			MailAccount ma = (MailAccount)getSelectedTO();
			return ObjectUtils.equals(getCurrentAccount(), ma);
		}
		return false;
	}
	
	public boolean isShowAccountList() throws ManagerBeanException {
		if ( mailAccounts == null ) {
			updateMailAccountList();
		}
		return mailAccounts.size() > 1;
	}
	
	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}

	public void updateMailAccountList() throws ManagerBeanException {
		this.mailAccounts = getUserMailAccounts();
		if ( AonUtil.isBeanValue(BEAN_WEBMAIL, SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY) ) {
			this.mailAccounts.addAll(0, getDomainMailAccounts());
		}
	}

	private List<SelectItem> loadMailAccountList() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			MailAccount ma = (MailAccount) to;
			String label = ma.getName() + " (" + ma.getEmail() + ")";
			SelectItem item = new SelectItem(ma, label);
			list.add(item);
		}
		return list;
	}	
	
	public void updateCurrentMailAccount() {
		if ( WebMailController.isConnectable() ) {
			WebMailController webmail = (WebMailController) AonUtil.getRegisteredBean(BEAN_WEBMAIL);
			if ( webmail.isLogged() ) {
				setCurrentAccount( webmail.getServer().getAccount() );			
			} else {
				setCurrentAccount(null);
			}
		}
	}
	
	public MailAccount getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(MailAccount currentAccount) {
		this.currentAccount = currentAccount;
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
		MailAccount newAccount = (MailAccount) event.getNewValue();
		for( int i = 0; i < this.mailAccounts.size(); i++ ) {
			if ( ObjectUtils.equals(newAccount, this.mailAccounts.get(i).getValue()) ) {
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
				return ObjectUtils.equals(getCurrentAccount(), account);
			}
		}
		return true;
	}
	
	public boolean isEditable() {
		MailAccount account = (MailAccount)getTo();
		if ( account.isDefault() ) {
			return isSystemAccountEditable();
		}
		return true;
	}

	public void loadFolders() {
		MailAccount account = (MailAccount) getTo();
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

	public void loadTree( AonServer server ) {
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
			BeanUtils.setProperty( getTo(), selectedFolder, folder.getFullName() );
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	public boolean isSystemAccountEditable() {
		return systemAccountEditable;
	}

	public void setSystemAccountEditable(boolean systemAccountEditable) {
		this.systemAccountEditable = systemAccountEditable;
	}
	
	@SuppressWarnings("unchecked")
	private void resetDefaults() throws ManagerBeanException {
		List<ITransferObject> list = (List<ITransferObject>) getModel().getWrappedData();
		for (ITransferObject to : list) {
			((MailAccount) to).setDefaultAccount(false);
			getManagerBean().update(to);
		}
	}

	public void onSetDefault(ActionEvent event) {
		try {
			resetDefaults();
			MailAccount account = (MailAccount) getModel().getRowData();
			account.setDefaultAccount(true);
			getManagerBean().update(account);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSetDefault exception: ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);			
		}
	}

	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new LdapTransferObjectConverter(this);			
		}
		return converter;
	}	

	private List<SelectItem> getUserMailAccounts() {
		AuthPrincipal auth = Utils.getAuthPrincipal();
		Name dn = NameResolver.getUserAccountsDN(auth.getDomain(), auth.getShortName());
		return getMailAccounts(dn);
	}
	
	private List<SelectItem> getDomainMailAccounts() {
		if ( domainMailAccounts == null ) {
			DomainResolver domainResolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
			Name dn = NameResolver.getDomainDN( domainResolver.getDomain() );
			this.domainMailAccounts = getMailAccounts(dn);
		}
		return domainMailAccounts;
	}
	
	private List<SelectItem> getMailAccounts( Name dn ) {
		Name oldDN = getLdapDAO().getBaseDN();
		try {
			if ( updateBaseDN( dn ) ) {
				return loadMailAccountList();	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading mail accounts of " + dn, e );
		} finally {
			getLdapDAO().setBaseDN(oldDN);
		}
		return new LinkedList<SelectItem>();
	}
	
}