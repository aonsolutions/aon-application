package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.financial.BankFinancialStatement;
import com.code.aon.ui.accounting.financial.CashFinancialStatement;
import com.code.aon.ui.accounting.financial.ExpensesFinancialStatement;
import com.code.aon.ui.accounting.financial.FinanceFinancialStatement;
import com.code.aon.ui.accounting.financial.FinancialStatementParams;
import com.code.aon.ui.accounting.financial.IFinancialStatementManager;
import com.code.aon.ui.accounting.financial.TaxFinancialStatement;
import com.code.aon.ui.util.AonUtil;

public class FinancialStatementController {

	private Month financialMonth;
	private int financialYear;
	private Date financeDate;
	private Month expensesMonth;
	private int expensesYear;
	List<IFinancialStatementManager> managers;
	private Date expensesDate;
	private Date financialDate;

	public Month getFinancialMonth() {
		return financialMonth;
	}

	public void setFinancialMonth(Month financialMonth) {
		this.financialMonth = financialMonth;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(int financialYear) {
		this.financialYear = financialYear;
	}

	public Date getFinanceDate() {
		return financeDate;
	}

	public void setFinanceDate(Date financeDate) {
		this.financeDate = financeDate;
	}

	public Month getExpensesMonth() {
		return expensesMonth;
	}

	public void setExpensesMonth(Month expensesMonth) {
		this.expensesMonth = expensesMonth;
	}

	public int getExpensesYear() {
		return expensesYear;
	}

	public void setExpensesYear(int expensesYear) {
		this.expensesYear = expensesYear;
	}

	public Date getExpensesDate() {
		return expensesDate;
	}

	public void setExpensesDate(Date expensesDate) {
		this.expensesDate = expensesDate;
	}

	public Date getFinancialDate() {
		return financialDate;
	}

	public void setFinancialDate(Date financialDate) {
		this.financialDate = financialDate;
	}

	public List<IFinancialStatementManager> getManagers() {
		if (managers == null) {
			managers = new LinkedList<IFinancialStatementManager>();
			managers.add(new BankFinancialStatement());
			managers.add(new CashFinancialStatement());
			managers.add(new FinanceFinancialStatement());
			managers.add(new ExpensesFinancialStatement());
			managers.add(new TaxFinancialStatement());
		}
		return managers;
	}

	public void setManagers(List<IFinancialStatementManager> managers) {
		this.managers = managers;
	}

	public void onReset(ActionEvent event) {
		expensesDate = null;
		financialDate = null;
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		financialMonth = Month.getMonthByValue(month);
		financialYear = year;
		expensesMonth = Month.getMonthByValue(month);
		expensesYear = year;
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, (month));
		c.set(Calendar.YEAR, year);
		financeDate = c.getTime();

		for (IFinancialStatementManager manager : getManagers()) {
			manager.initialize();
		}
	}

	public void onSearch(ActionEvent event) {
		try {

			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, getFinancialMonth().getValue());
			c.set(Calendar.YEAR, getFinancialYear());
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DAY_OF_MONTH, -1);
			financialDate = c.getTime();

			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, getExpensesMonth().getValue());
			c.set(Calendar.YEAR, getExpensesYear());
			expensesDate = c.getTime();

			if (financialDate.before(expensesDate)) {
				String msg = "La fecha de inclusión de gastos, no puede ser superior a la fecha del estado financiero";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (financialDate.before(financeDate)) {
				String msg = "La fecha de exclusión de vencimientos, no puede ser superior a la fecha del estado financiero";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}

			FinancialStatementParams params = new FinancialStatementParams();

			params.setFinancialDate(financialDate);
			params.setIncludeExpensesDate(expensesDate);
			params.setExcludeFinanceDate(financeDate);

			for (IFinancialStatementManager manager : getManagers()) {
				manager.search(params);
			}

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public double getTotal() {
		double total = 0;
		for (IFinancialStatementManager manager : getManagers()) {
			total = CommonUtil.round(total + manager.getTotalForSummary());
		}
		return total;
	}

}
