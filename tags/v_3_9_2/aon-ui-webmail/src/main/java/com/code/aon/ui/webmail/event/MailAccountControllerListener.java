package com.code.aon.ui.webmail.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountController;
import com.code.aon.ui.webmail.controller.SignatureController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class MailAccountControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		MailAccountController controller = (MailAccountController) event.getController();
		try {
			controller.updateMailAccountList();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}					
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		updateSignatureList();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		updateSignatureList();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateMailAccount( (MailAccount) event.getController().getTo() );
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		MailAccount mailAccount = (MailAccount) event.getController().getTo(); 
		updateMailAccount( mailAccount );
		WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_WEBMAIL);
		if ( mailAccount.getId().equals(wmc.getServer().getAccount().getId()) ) {
			wmc.getServer().setAccount(mailAccount);
		}
	}

	private void updateSignatureList() throws ControllerListenerException {
		SignatureController signatureController = (SignatureController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_SIGNATURE);
		try {
			signatureController.updateSignatureList();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	private void updateMailAccount( MailAccount mailAccount ) throws ControllerListenerException {
		SignatureController signatureController = (SignatureController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_SIGNATURE);
		try {
			String id = mailAccount.getSignature().getId();			
			Signature signature = (Signature) signatureController.getManagerBean().get( id );
			if ( signature != null ) {
				mailAccount.setSignature(signature);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}