package com.code.aon.ui.webmail.event;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.IMailAccount;

public class MailAccountControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		updateMailAccountList();
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
		updateFolderTree(event);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		updateMailAccountList();
		if ( WebMailController.isConnectable() ) {
			IMailAccount mailAccount = (IMailAccount) event.getController().getTo();
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
			if ( (wmc.getServer() != null) && mailAccount.equals(wmc.getServer().getAccount()) ) {
				wmc.getServer().setAccount(mailAccount);
			}
		}
		updateFolderTree(event);
	}

	private void updateSignatureList() {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
		mailConfig.updateSignatureList();
	}

	private void updateMailAccountList() {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
		mailConfig.updateMailAccountList();
	}
	
	private void updateFolderTree(ControllerEvent event) {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		mailConfig.loadFolders();
	}
	
}