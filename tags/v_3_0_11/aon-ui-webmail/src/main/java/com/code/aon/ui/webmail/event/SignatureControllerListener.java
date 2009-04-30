package com.code.aon.ui.webmail.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class SignatureControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Signature signature = (Signature) event.getController().getTo(); 
		WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		MailAccount account = wmc.getServer().getAccount();
		if ( signature.getId().equals(account.getSignature().getId()) ) {
			account.setSignature(signature);
		}
	}
	
}