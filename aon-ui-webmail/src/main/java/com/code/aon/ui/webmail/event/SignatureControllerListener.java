package com.code.aon.ui.webmail.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;

public class SignatureControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		if ( WebMailController.isConnectable() ) {		
			ISignature signature = (ISignature) event.getController().getTo(); 
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
			IMailAccount account = wmc.getServer().getAccount();
			if ( signature.equals(account.getISignature()) ) {
				account.setISignature(signature);
			}
		}
	}
	
}