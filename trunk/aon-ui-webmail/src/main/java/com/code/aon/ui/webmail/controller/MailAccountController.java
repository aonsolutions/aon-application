package com.code.aon.ui.webmail.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.enumeration.MailAccountStatus;

public class MailAccountController extends BasicController {

	private String error;
	
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
		SignatureController signatureController = (SignatureController)AonUtil.getRegisteredBean(AonConstants.BEAN_SIGNATURE);
		signatureController.initializeModel();
		signatureController.onSearch(null);
    	BasicController emailController = (BasicController)AonUtil.getRegisteredBean(AonConstants.BEAN_CONTACT);
    	emailController.initializeModel();
    	emailController.onSearch(null);
		if (error==null){
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AonConstants.NAVIGATION_FOLDER);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getStatusTypes(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> types = new LinkedList<SelectItem>();
		for(MailAccountStatus type_ : MailAccountStatus.values()){
			SelectItem item = new SelectItem(type_, type_.getName(locale));
			types.add(item);
		}
		return types;
	}

	public boolean isToDefaultAccount(){
		MailAccount account = (MailAccount)getTo();
		if (account.getStatus()!=null &&
				account.getStatus().compareTo(MailAccountStatus.ACTIVE)==0){
			return true;
		}
		return false;
	}

	public boolean isCurrentToDefaultAccount(){
		MailAccount account = (MailAccount)getSelectedTO();
		if (account.getStatus()!=null &&
				account.getStatus().compareTo(MailAccountStatus.ACTIVE)==0){
			return true;
		}
		return false;
	}

}
