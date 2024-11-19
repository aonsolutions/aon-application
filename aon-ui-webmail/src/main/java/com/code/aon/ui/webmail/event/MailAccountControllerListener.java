package com.code.aon.ui.webmail.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.webmail.IMailAccount;

public class MailAccountControllerListener extends ControllerAdapter {

	public static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private MailConfigController getMailConfig() {
		return (MailConfigController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		getMailConfig().updateSignatureList();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		updateFolderTree(event);
		getMailConfig().updateSignatureList();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateFolderTree(event);
		getMailConfig().setMailAccounts(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		getMailConfig().setMailAccounts(null);
		updateFolderTree(event);
	}
	
	private void updateFolderTree(ControllerEvent event) {
		IMailAccount mailAccount = (IMailAccount) event.getController().getTo();
		if ( mailAccount.isIMAP() ) {
			getMailConfig().loadFolders();
		}
	}
	
	
	
}