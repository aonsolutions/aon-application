package com.code.aon.ui.account.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.AmortizationType;
import com.code.aon.account.AutoConcept;
import com.code.aon.account.Leasing;
import com.code.aon.account.Loan;
import com.code.aon.account.Period;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.account.enumeration.AmortizationPeriod;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * Collections controller.
 * 
 * @author Consulting & Development.
 */
public class AccountCollectionsController {

	private LinkedList<SelectItem> accountLevels;

	/**
	 * Gets the sales accounts.
	 * 
	 * @return the sales accounts
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	public List<SelectItem> getSalesAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> salesAccounts = new LinkedList<SelectItem>();
		salesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "70*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			salesAccounts.add(item);
		}
		return salesAccounts;
	}

	/**
	 * Gets the purchase accounts.
	 * 
	 * @return the purchase accounts
	 * @throws ExpressionException
	 * @throws ManagerBeanException
	 */

	public List<SelectItem> getPurchaseAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> purchaseAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "60*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			purchaseAccounts.add(item);
		}
		return purchaseAccounts;
	}

	public List<SelectItem> getCashAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> cashAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "570*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			cashAccounts.add(item);
		}
		return cashAccounts;
	}

	/**
	 * Gets the expenses accounts.
	 * 
	 * @return the expenses accounts
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */

	public List<SelectItem> getExpensesAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> expensesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

		Expression expression1 = ExpressionUtilities.getLikeExpression(accountBean
				.getFieldName(IAccountAlias.ACCOUNT_ID), "62%");
		Expression expression2 = ExpressionUtilities.getLikeExpression(accountBean
				.getFieldName(IAccountAlias.ACCOUNT_ID), "66%");
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getOrExpression(expression1, expression2));
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			expensesAccounts.add(item);
		}
		return expensesAccounts;
	}

	public List<SelectItem> getChargedVatAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> chargedVatAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "477*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			chargedVatAccounts.add(item);
		}
		return chargedVatAccounts;
	}

	public List<SelectItem> getPaidVatAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> paidVatAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "472*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			paidVatAccounts.add(item);
		}
		return paidVatAccounts;
	}

	public List<SelectItem> getPaidRetentionAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> paidRetentionAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "473*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			paidRetentionAccounts.add(item);
		}
		return paidRetentionAccounts;
	}

	public List<SelectItem> getChargedRetentionAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> chargedRetentionAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "475*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			chargedRetentionAccounts.add(item);
		}
		return chargedRetentionAccounts;
	}

	public List<SelectItem> getSalaryAccounts() throws ManagerBeanException, ExpressionException {
		List<SelectItem> salaryAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "640*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			salaryAccounts.add(item);
		}
		return salaryAccounts;
	}

	public List<SelectItem> getPendingSalaryAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> pendingSalaryAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "465*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			pendingSalaryAccounts.add(item);
		}
		return pendingSalaryAccounts;
	}

	public List<SelectItem> getSocialInsuranceAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> socialInsuranceAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "476*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			socialInsuranceAccounts.add(item);
		}
		return socialInsuranceAccounts;
	}

	public List<SelectItem> getEnterpriseSocialInsuranceAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> enterpriseSocialInsuranceAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "642*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			enterpriseSocialInsuranceAccounts.add(item);
		}
		return enterpriseSocialInsuranceAccounts;
	}

	public List<SelectItem> getFixedAssestsAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> fixedAssestsAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "21*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			fixedAssestsAccounts.add(item);
		}
		return fixedAssestsAccounts;
	}

	public List<SelectItem> getDebtInterestAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> debtInterestAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "662*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			debtInterestAccounts.add(item);
		}
		return debtInterestAccounts;
	}

	public List<SelectItem> getFinancialExpensesAccounts() throws ManagerBeanException,
			ExpressionException {
		List<SelectItem> financialExpensesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "669*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			financialExpensesAccounts.add(item);
		}
		return financialExpensesAccounts;
	}

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
		criteria.addOrder(periodBean.getFieldName(IAccountAlias.PERIOD_ID), false);
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
		criteria.addOrder(conceptBean.getFieldName(IAccountAlias.AUTO_CONCEPT_DESCRIPTION), false);
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
		criteria.addOrder(periodBean.getFieldName(IAccountAlias.PERIOD_ID), false);
		Iterator<?> iter = periodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Period period = (Period) iter.next();
			SelectItem item = new SelectItem(period.getId(), period.getId());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	public List<SelectItem> getAccountTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		AccountEntryType[] accountEntryTypes = AccountEntryType.values();
		for (int i = 0; i < accountEntryTypes.length; i++) {
			AccountEntryType type = accountEntryTypes[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
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

	public List<SelectItem> getLoans() throws ManagerBeanException, ExpressionException {
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

	public List<SelectItem> getLeasings() throws ManagerBeanException, ExpressionException {
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

	public List<SelectItem> getAmortizationTypes() throws ManagerBeanException, ExpressionException {
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
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> list = new LinkedList<SelectItem>();
		AmortizationPeriod[] periods = AmortizationPeriod.values();
		for (int i = 0; i < periods.length; i++) {
			AmortizationPeriod type = periods[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			list.add(item);
		}
		return list;
	}
}