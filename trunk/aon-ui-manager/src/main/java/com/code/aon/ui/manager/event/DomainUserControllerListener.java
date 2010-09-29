package com.code.aon.ui.manager.event;

import com.code.aon.manager.DomainUser;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.SignatureController;

public class DomainUserControllerListener extends ControllerAdapter {

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
	
	private void updateWebmail( DomainUser user ) {
		SignatureController sc = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
		sc.updateBaseDN(user.getId());
		sc.onSearch(null);
	}	

}
