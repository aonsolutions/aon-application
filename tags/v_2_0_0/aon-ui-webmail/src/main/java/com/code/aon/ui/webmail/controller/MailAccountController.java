package com.code.aon.ui.webmail.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.enumeration.MailAccountStatus;
import com.icesoft.faces.component.ext.RowSelectorEvent;

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
	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AonConstants.NAVIGATION_MAILACCOUNT_FORM);
	}
	
	@SuppressWarnings("unused")
	public void onChangeServer(RowSelectorEvent event){
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
		super.onSelect(new ActionEvent(event.getComponent()));
		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		webmail.getServer().disconnect();
		MailAccount previous = webmail.getServer().getAccount();
		try{
			webmail.init((MailAccount)super.getTo());
		}catch (Exception e) {
			error = e.getMessage();
			webmail.init((MailAccount)previous);
		}
    	TreeController treeController = (TreeController)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
    	try {
			treeController.loadTree();
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
		SignatureController signatureController = (SignatureController)AonUtil.getRegisteredBean(AonConstants.BEAN_SIGNATURE);
		signatureController.initializeModel();
		signatureController.onSearch(null);
    	ICEController emailController = (ICEController)AonUtil.getRegisteredBean(AonConstants.BEAN_EMAIL);
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
