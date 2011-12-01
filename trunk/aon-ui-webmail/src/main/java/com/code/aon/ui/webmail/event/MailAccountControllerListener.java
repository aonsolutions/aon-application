package com.code.aon.ui.webmail.event;

import javax.naming.Name;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailAccountController;
import com.code.aon.ui.webmail.controller.SignatureController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class MailAccountControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		updateMailAccountList(event);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		updateSignatureList();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		updateFolderTree(event);
		updateSignatureList();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateSignature( (MailAccount) event.getController().getTo() );
		updateFolderTree(event);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		MailAccount mailAccount = (MailAccount) event.getController().getTo(); 
		updateSignature( mailAccount );
		updateMailAccountList(event);
		if ( WebMailController.isConnectable() ) {
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
			if ( mailAccount.getId().equals(wmc.getServer().getAccount().getId()) ) {
				wmc.getServer().setAccount(mailAccount);
			}
		}
		updateFolderTree(event);
	}

	private void updateSignatureList() throws ControllerListenerException {
		SignatureController signatureController = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
		try {
			signatureController.updateSignatureList();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	private void updateMailAccountList(ControllerEvent event) throws ControllerListenerException {
		MailAccountController controller = (MailAccountController) event.getController();
		try {
			controller.updateMailAccountList();
			controller.updateCurrentMailAccount();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	private void updateSignature( MailAccount mailAccount ) throws ControllerListenerException {
		Signature signature = mailAccount.getSignature();
		if ( (signature != null) && (signature.getId() != null) ) {
			SignatureController signatureController = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
			try {
				Name id = signature.getId();			
				Signature newSignature = (Signature) signatureController.getManagerBean().get( id );
				if ( newSignature != null ) {
					mailAccount.setSignature(newSignature);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}
	
	private void updateFolderTree(ControllerEvent event) {
		MailAccountController controller = (MailAccountController) event.getController();
		controller.loadFolders();
	}
	
}