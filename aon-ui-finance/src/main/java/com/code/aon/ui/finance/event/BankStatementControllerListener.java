package com.code.aon.ui.finance.event;

import com.code.aon.finance.BankStatement;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ui.finance.controller.BankStatementController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BankStatementControllerListener extends ControllerAdapter {

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		controller.setModel(null);
		controller.setRegistryBank(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		bankStatement.setRegistryBank(controller.getRegistryBank());
		bankStatement.setOperationDate(controller.getOperationDate());
		bankStatement.setPayment(true);
		bankStatement.setStatus(StatementStatus.PENDING);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		bankStatement.setRegistryBank(controller.getRegistryBank());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		controller.setOperationDate(bankStatement.getOperationDate());
	}

}
