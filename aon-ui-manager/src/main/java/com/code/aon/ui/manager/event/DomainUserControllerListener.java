package com.code.aon.ui.manager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.manager.DomainUser;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountController;
import com.code.aon.ui.webmail.controller.SignatureController;
import com.code.aon.webmail.Signature;

public class DomainUserControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserControllerListener.class);
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		duc.setWebmail( duc.hasWebmail(user) );
		if ( duc.isWebmail() ) {
			updateWebmail(user);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getManager().resetTermsOfServiceAccepted();
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.resetPassword( duc.getDomainUser() );
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		try {		
			duc.registerUserInApplication(user, AON_DESKTOP, USUARIO_PROFILE);
			duc.registerUserInApplication(user, AON_WEBMAIL, USUARIO_PROFILE);
			duc.registerScopeInDBs(user.getUid(), GENERAL_SCOPE);
			duc.createMailAccount(user);
			Signature signature = duc.addDefaultSignature(user);
			duc.addDefaultMailAccount(user, signature);
			duc.setWebmail(true);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getManager().getLogger().domainUserAddded(user);
		updateWebmail(user);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		try {
			duc.deactiveDBUser(user);
			duc.removeMailAccount(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
		getManager().getLogger().domainUserdRemoved(user);
	}

	private void updateWebmail( DomainUser user ) {
		SignatureController sc = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
		sc.updateBaseDN(user.getId());
		sc.onSearch(null);
		MailAccountController mac = (MailAccountController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT);
		mac.updateBaseDN(user.getId());
		mac.onSearch(null);
		ContactController cc = (ContactController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_CONTACT);
		cc.updateBaseDN(user.getId());
		cc.onSearch(null);
	}	

}
