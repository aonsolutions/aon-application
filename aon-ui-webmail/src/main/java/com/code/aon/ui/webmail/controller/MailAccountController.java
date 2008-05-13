package com.code.aon.ui.webmail.controller;

import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.IDAO;
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

	private static final Logger LOGGER = Logger.getLogger(MailAccountController.class.getName());
	
	private String error;
	
	private BasicManagerBean ldapManagerBean;

	public IDAO getDAO( AuthPrincipal principal ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		DistinguishedName baseDN = AonDN.getUserAccountsDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "MailAccount DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			IDAO dao = getDAO(LoginController.getPrincipal());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
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
			webmail.init((MailAccount)super.getSelectedTO());
		}catch (Exception e) {
			error = e.getMessage();
			webmail.init((MailAccount)previous);
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
