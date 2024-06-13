package com.code.aon.ui.finance.event;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.finance.controller.BankStatementController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class BankStatementControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		controller.setAccount(new Account());
		controller.setModel(null);
		controller.setRegistryBank(null);
		controller.setBankStatementLinkManager(null);
		controller.clearCheckedBankStatement();
		controller.resetErrors();
	}

	@Override
	public void beforeModelSearched(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		controller.clearCheckedBankStatement();
		controller.resetErrors();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		bankStatement.setRegistryBank(controller.getRegistryBank());
		bankStatement.setOperationDate(controller.getOperationDate());
		bankStatement.setPayment(true);
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
		bankStatement.setSecurityLevel(SecurityLevel.OFFICIAL);
		bankStatement.setStatus(StatementStatus.PENDING);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		bankStatement.setRegistryBank(controller.getRegistryBank());
		if (bankStatement.getSecurityLevel() == null) {
			bankStatement.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		controller.setOperationDate(bankStatement.getOperationDate());

		try {
			controller.getCriteria().addOrExpression(controller.getFieldName(IEntityAlias.BANK_STATEMENT_ID), bankStatement.getId().toString());
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch(ExpressionException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
