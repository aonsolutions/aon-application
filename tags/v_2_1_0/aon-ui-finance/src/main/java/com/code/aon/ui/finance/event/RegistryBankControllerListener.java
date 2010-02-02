package com.code.aon.ui.finance.event;

import com.code.aon.finance.RegistryBank;
import com.code.aon.ui.finance.controller.RegistryBankController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryBankControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryBankController bankController = (RegistryBankController)event.getController();
		((RegistryBank)bankController.getTo()).setBankAccount(createAccount(bankController));
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		RegistryBankController bankController = (RegistryBankController)event.getController();
		((RegistryBank)bankController.getTo()).setBankAccount(createAccount(bankController));
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		RegistryBankController rBankController = (RegistryBankController)event.getController();
		RegistryBank rBank = (RegistryBank)rBankController.getTo();
		rBankController.setEntity(rBank.getBankAccount().substring(0,4));
		rBankController.setOffice(rBank.getBankAccount().substring(4,8));
		rBankController.setControl(rBank.getBankAccount().substring(8,10));
		rBankController.setAccount(rBank.getBankAccount().substring(10,20));
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryBankController rBankController = (RegistryBankController)event.getController();
		rBankController.setEntity("");
		rBankController.setOffice("");
		rBankController.setControl("");
		rBankController.setAccount("");
	}
	
	private String createAccount(RegistryBankController bankController) {
		return bankController.getEntity() + bankController.getOffice() + bankController.getControl() + bankController.getAccount();
	}
}
