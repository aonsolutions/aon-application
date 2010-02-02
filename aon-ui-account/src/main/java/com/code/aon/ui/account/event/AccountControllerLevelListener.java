package com.code.aon.ui.account.event;

import com.code.aon.account.Account;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerLevelListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account)event.getController().getTo();
		if(!checkValidLength(account)){
			throw new ControllerListenerException("Valid id lenghts are: 1, 2, 3, 5 or 12");
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account)event.getController().getTo();
		if(!checkValidLength(account)){
			throw new ControllerListenerException("Valid id lenghts are: 1, 2, 3, 5 or 12");
		}
	}

	private boolean checkValidLength(Account account) {
		if(account.getId().length() == 1 || account.getId().length() == 2 || account.getId().length() == 3 ||
				account.getId().length() == 5 || account.getId().length() == 12 ){
			return true;
		}
		return false;
	}
}
