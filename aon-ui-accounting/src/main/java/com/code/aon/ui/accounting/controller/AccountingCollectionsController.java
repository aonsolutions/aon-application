package com.code.aon.ui.accounting.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

/**
 * Collections controller.
 * 
 * @author Consulting & Development.
 */
public class AccountingCollectionsController {

	private LinkedList<SelectItem> accountLevels;
	private LinkedList<SelectItem> balanceTypes;
	private LinkedList<SelectItem> accountEntryTypes;
	private LinkedList<SelectItem> amortizationPeriods;

	/**
	 * Gets the account periods.
	 * 
	 * @return the expenses accounts
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */

	public List<SelectItem> getAccountPeriods() throws ManagerBeanException, ExpressionException {
		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), false);
		Iterator<?> iter = periodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Period period = (Period) iter.next();
			SelectItem item = new SelectItem(period, period.getId());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	/**
	 * Gets the account periods.
	 * 
	 * @return the expenses accounts
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */

	public List<SelectItem> getAutoConcepts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> autoConcepts = new LinkedList<SelectItem>();
		IManagerBean conceptBean = BeanManager.getManagerBean(AutoConcept.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(conceptBean.getFieldName(IAccountingAlias.AUTO_CONCEPT_DESCRIPTION), false);
		Iterator<?> iter = conceptBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			AutoConcept concept = (AutoConcept) iter.next();
			SelectItem item = new SelectItem(concept, concept.getDescription());
			autoConcepts.add(item);
		}
		return autoConcepts;
	}

	/**
	 * Gets the account periods.
	 * 
	 * @return the expenses accounts
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	public List<SelectItem> getAccountPeriodKeys() throws ManagerBeanException, ExpressionException {
		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), false);
		Iterator<?> iter = periodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Period period = (Period) iter.next();
			SelectItem item = new SelectItem(period.getId(), period.getId());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	public List<SelectItem> getAccountTypes() {
		if (accountEntryTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			accountEntryTypes = new LinkedList<SelectItem>();
			AccountEntryType[] aeTypes = AccountEntryType.values();
			for (int i = 0; i < aeTypes.length; i++) {
				AccountEntryType type = aeTypes[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				accountEntryTypes.add(item);
			}
		}
		return accountEntryTypes;
	}

	public List<SelectItem> getAccountLevels() {
		if (accountLevels == null) {
			accountLevels = new LinkedList<SelectItem>();
			accountLevels.add(new SelectItem(1, "1"));
			accountLevels.add(new SelectItem(2, "2"));
			accountLevels.add(new SelectItem(3, "3"));
			accountLevels.add(new SelectItem(4, "4"));
			accountLevels.add(new SelectItem(5, "5"));
		}
		return accountLevels;
	}

	public List<SelectItem> getLoans() throws ManagerBeanException {
		List<SelectItem> loans = new LinkedList<SelectItem>();
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		Iterator<?> iter = loanBean.getList(null).iterator();
		while (iter.hasNext()) {
			Loan loan = (Loan) iter.next();
			SelectItem item = new SelectItem(loan, loan.getDescription());
			loans.add(item);
		}
		return loans;
	}

	public List<SelectItem> getLeasings() throws ManagerBeanException{
		List<SelectItem> loans = new LinkedList<SelectItem>();
		IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
		Iterator<?> iter = leasingBean.getList(null).iterator();
		while (iter.hasNext()) {
			Leasing leasing = (Leasing) iter.next();
			SelectItem item = new SelectItem(leasing, leasing.getDescription());
			loans.add(item);
		}
		return loans;
	}

	public List<SelectItem> getInvoiceTypes(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.EXPENSES, InvoiceType.EXPENSES.getName(locale));
		types.add(item);
		return types;
	}

	public List<SelectItem> getAmortizationTypes() throws ManagerBeanException {
		List<SelectItem> ats = new LinkedList<SelectItem>();
		IManagerBean atBean = BeanManager.getManagerBean(AmortizationType.class);
		Iterator<?> iter = atBean.getList(null).iterator();
		while (iter.hasNext()) {
			AmortizationType at = (AmortizationType) iter.next();
			SelectItem item = new SelectItem(at, at.getDescription());
			ats.add(item);
		}
		return ats;
	}
	
	public List<SelectItem> getAmortizationPeriods() {
		if (amortizationPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			amortizationPeriods = new LinkedList<SelectItem>();
			AmortizationPeriod[] periods = AmortizationPeriod.values();
			for (int i = 0; i < periods.length; i++) {
				AmortizationPeriod type = periods[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				amortizationPeriods.add(item);
			}
		}
		return amortizationPeriods;
	}
	
	public List<SelectItem> getClosingBalances() throws ManagerBeanException {
		return getBalances(BalanceType.CLOSING );
	}
	public List<SelectItem> getOperatingBalances() throws ManagerBeanException {
		return getBalances(BalanceType.OPERATING );
	}
	public List<SelectItem> getCustomBalances() throws ManagerBeanException {
		return getBalances(BalanceType.CUSTOM );
	}
	private List<SelectItem> getBalances(BalanceType balanceType) throws ManagerBeanException {
		List<SelectItem> balances = new LinkedList<SelectItem>();
		IManagerBean balanceBean = BeanManager.getManagerBean(Balance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(balanceBean.getFieldName(IAccountingAlias.BALANCE_TYPE), balanceType);
		Iterator<?> iter = balanceBean.getList(c).iterator();
		while (iter.hasNext()) {
			Balance b = (Balance) iter.next();
			SelectItem item = new SelectItem(b, b.getName());
			balances.add(item);
		}
		return balances;
	}
	public List<SelectItem> getBalanceTypes() {
		if (balanceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			balanceTypes = new LinkedList<SelectItem>();
			BalanceType[] bTypes = BalanceType.values();
			for (int i = 0; i < bTypes.length; i++) {
				BalanceType type = bTypes[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				balanceTypes.add(item);
			}
		}
		return balanceTypes;
	}
	
}