package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Expense;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.ExpenseStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class ExpenseSearchListener extends ControllerSearchListener {

	private Registry registry;
	private ExpenseStatus[] expenseStatuses;	
	private Expense expense;

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public ExpenseStatus[] getExpenseStatuses() {
		return expenseStatuses;
	}

	public void setExpenseStatuses(ExpenseStatus[] expenseStatuses) {
		this.expenseStatuses = expenseStatuses;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setRegistry(new Registry());
		setExpense(new Expense());
		ExpenseStatus[] defaultExpenseStatus = {ExpenseStatus.PENDING};
		setExpenseStatuses(defaultExpenseStatus);		
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getRegistry() != null && getRegistry().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_REGISTRY_ID), getRegistry().getId());			
		}
		if (getExpense() != null && getExpense().getId() != null) {
			String expense = getController().resolveAlias("ExpenseAccount_lines_expense_id");
			criteria.addEqualExpression(expense,getExpense().getId());			
		}
		if (!ArrayUtils.isEmpty(getExpenseStatuses())) {
			String status = getController().resolveAlias(ICommercialAlias.EXPENSE_ACCOUNT_STATUS);
			addEnumToCriteria(criteria, status, getExpenseStatuses());
		}
	}	

}