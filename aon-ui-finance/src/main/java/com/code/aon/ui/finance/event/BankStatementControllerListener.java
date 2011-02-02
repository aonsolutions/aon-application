package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.util.ExpressionException;
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
		controller.setBankStatementLinkManager(null);
		controller.clearCheckedBankStatement();
		controller.resetErrors();
	}

	@Override
	public void beforeModelSearched(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		controller.clearCheckedBankStatement();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		BankStatementController controller = (BankStatementController)event.getController();
		BankStatement bankStatement = (BankStatement)controller.getTo();
		bankStatement.setRegistryBank(controller.getRegistryBank());
		bankStatement.setOperationDate(controller.getOperationDate());
		bankStatement.setPayment(true);
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
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

		try {
			controller.getCriteria().addOrExpression(controller.getFieldName(IFinanceAlias.BANK_STATEMENT_ID), bankStatement.getId().toString());
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch(ExpressionException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
