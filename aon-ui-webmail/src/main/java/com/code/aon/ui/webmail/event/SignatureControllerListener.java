package com.code.aon.ui.webmail.event;

import static com.code.aon.ui.common.ICommonMessages.SIGNATURE_USED;

import javax.faces.event.AbortProcessingException;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ISignatureController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;

public class SignatureControllerListener extends ControllerAdapter {

	public static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private MailConfigController getMailConfig() {
		return (MailConfigController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		if ( getMailConfig().isConnectable() ) {		
			ISignature signature = (ISignature) event.getController().getTo(); 
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
			if ( wmc.getServer() != null ) {
				IMailAccount account = wmc.getServer().getAccount();
				if ( signature.equals(account.getISignature()) ) {
					account.setISignature(signature);
				}				
			}
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ISignatureController controller = (ISignatureController) event.getController();
		ISignature signature = (ISignature) controller.getTo();
		if (! controller.isRemovable(signature) ) {
			String message = AonUtil.addErrorMessageFromBundle( SIGNATURE_USED, signature.getName() );
			throw new AbortProcessingException( message );			
		}
	}
	
}