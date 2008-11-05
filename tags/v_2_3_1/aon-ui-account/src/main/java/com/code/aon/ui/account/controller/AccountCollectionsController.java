package com.code.aon.ui.account.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.Period;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
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
	
	/**
	 * Gets the sales accounts.
	 * 
	 * @return the sales accounts
	 * @throws ManagerBeanException 
	 * @throws ExpressionException 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSalesAccounts() throws ManagerBeanException, ExpressionException {
		List salesAccounts = new LinkedList<SelectItem>();
		salesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "70*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
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
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPurchaseAccounts() throws ManagerBeanException, ExpressionException {
		List purchaseAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "60*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			purchaseAccounts.add(item);
		}
		return purchaseAccounts;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCashAccounts() throws ManagerBeanException, ExpressionException {
		List cashAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "570*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
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
	@SuppressWarnings("unchecked")
	public List<SelectItem> getExpensesAccounts() throws ManagerBeanException, ExpressionException {
		List expensesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

		Expression expression1 = ExpressionUtilities.getLikeExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "62%");
		Expression expression2 = ExpressionUtilities.getLikeExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "66%");
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getOrExpression(expression1, expression2));
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			expensesAccounts.add(item);
		}
		return expensesAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getChargedVatAccounts() throws ManagerBeanException, ExpressionException {
		List chargedVatAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "477*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			chargedVatAccounts.add(item);
		}
		return chargedVatAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPaidVatAccounts() throws ManagerBeanException, ExpressionException{
		List paidVatAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "472*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			paidVatAccounts.add(item);
		}
		return paidVatAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPaidRetentionAccounts() throws ManagerBeanException, ExpressionException{
		List paidRetentionAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "473*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			paidRetentionAccounts.add(item);
		}
		return paidRetentionAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getChargedRetentionAccounts() throws ManagerBeanException, ExpressionException {
		List chargedRetentionAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "475*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			chargedRetentionAccounts.add(item);
		}
		return chargedRetentionAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSalaryAccounts() throws ManagerBeanException, ExpressionException{
		List salaryAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "640*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			salaryAccounts.add(item);
		}
		return salaryAccounts;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPendingSalaryAccounts() throws ManagerBeanException, ExpressionException{
		List pendingSalaryAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "465*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			pendingSalaryAccounts.add(item);
		}
		return pendingSalaryAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSocialInsuranceAccounts() throws ManagerBeanException, ExpressionException {
		List socialInsuranceAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "476*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			socialInsuranceAccounts.add(item);
		}
		return socialInsuranceAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getEnterpriseSocialInsuranceAccounts() throws ManagerBeanException, ExpressionException {
		List enterpriseSocialInsuranceAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "642*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			enterpriseSocialInsuranceAccounts.add(item);
		}
		return enterpriseSocialInsuranceAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getFixedAssestsAccounts() throws ManagerBeanException, ExpressionException {
		List fixedAssestsAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "21*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			fixedAssestsAccounts.add(item);
		}
		return fixedAssestsAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getDebtInterestAccounts() throws ManagerBeanException, ExpressionException {
		List debtInterestAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "662*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
			debtInterestAccounts.add(item);
		}
		return debtInterestAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getFinancialExpensesAccounts() throws ManagerBeanException, ExpressionException {
		List financialExpensesAccounts = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "669*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		Iterator iter = accountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
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
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAccountPeriods() throws ManagerBeanException, ExpressionException {
		List accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(periodBean.getFieldName(IAccountAlias.PERIOD_ID), false);
		Iterator iter = periodBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Period period = (Period)iter.next();
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
	
	public List<SelectItem> getSecurityLevels() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> levels = new LinkedList<SelectItem>();
		for (SecurityLevel level : SecurityLevel.values()) {
			String name = level.getName(locale);
			SelectItem item = new SelectItem(level, name);
			levels.add(item);
		}
		return levels;
	}
}