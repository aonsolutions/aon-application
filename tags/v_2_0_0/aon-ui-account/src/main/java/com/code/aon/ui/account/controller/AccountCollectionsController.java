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
	
	/** The sales accounts. */
	private List<SelectItem> salesAccounts;
	
	/** The purchase accounts. */
	private List<SelectItem> purchaseAccounts;
	
	/** The expenses accounts. */
	private List<SelectItem> expensesAccounts;
	
	private List<SelectItem> chargedVatAccounts;

	private List<SelectItem> paidVatAccounts;
	
	private List<SelectItem> paidRetentionAccounts;
	
	private List<SelectItem> chargedRetentionAccounts;
	
	private List<SelectItem> salaryAccounts;
	
	private List<SelectItem> pendingSalaryAccounts;
	
	private List<SelectItem> socialInsuranceAccounts;
	
	/** The account periods. */
	private List<SelectItem> accountPeriods;
	
	
	/**
	 * Gets the sales accounts.
	 * 
	 * @return the sales accounts
	 * @throws ManagerBeanException 
	 * @throws ExpressionException 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSalesAccounts() throws ManagerBeanException, ExpressionException {
		if(this.salesAccounts == null){
			this.salesAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "700*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.salesAccounts.add(item);
			}
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
		if(this.purchaseAccounts == null){
			this.purchaseAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "600*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.purchaseAccounts.add(item);
			}
		}
		return purchaseAccounts;
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
		if(this.expensesAccounts == null){
			this.expensesAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "6*");
			Expression expression = ExpressionUtilities.getNotEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "600*");
			criteria.addExpression(expression);
			expression = ExpressionUtilities.getNotEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "600");
			criteria.addExpression(expression);
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.expensesAccounts.add(item);
			}
		}
		return expensesAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getChargedVatAccounts() throws ManagerBeanException, ExpressionException {
		if(this.chargedVatAccounts == null){
			this.chargedVatAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "477*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.chargedVatAccounts.add(item);
			}
		}
		return chargedVatAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPaidVatAccounts() throws ManagerBeanException, ExpressionException{
		if(this.paidVatAccounts == null){
			this.paidVatAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "472*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.paidVatAccounts.add(item);
			}
		}
		return paidVatAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPaidRetentionAccounts() throws ManagerBeanException, ExpressionException{
		if(this.paidRetentionAccounts == null){
			this.paidRetentionAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "473*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.paidRetentionAccounts.add(item);
			}
		}
		return paidRetentionAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getChargedRetentionAccounts() throws ManagerBeanException, ExpressionException {
		if(this.chargedRetentionAccounts == null){
			this.chargedRetentionAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "475*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.chargedRetentionAccounts.add(item);
			}
		}
		return chargedRetentionAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSalaryAccounts() throws ManagerBeanException, ExpressionException{
		if(this.salaryAccounts == null){
			this.salaryAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "640*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.salaryAccounts.add(item);
			}
		}
		return salaryAccounts;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPendingSalaryAccounts() throws ManagerBeanException, ExpressionException{
		if(this.pendingSalaryAccounts == null){
			this.pendingSalaryAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "465*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.pendingSalaryAccounts.add(item);
			}
		}
		return pendingSalaryAccounts;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSocialInsuranceAccounts() throws ManagerBeanException, ExpressionException {
		if(this.socialInsuranceAccounts == null){
			this.socialInsuranceAccounts = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "476*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			Iterator iter = accountBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				SelectItem item = new SelectItem(account.getId(), account.getId() + " " + account.getDescription());
				this.socialInsuranceAccounts.add(item);
			}
		}
		return socialInsuranceAccounts;
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
		if(this.accountPeriods == null){
			this.accountPeriods = new LinkedList<SelectItem>();
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
            Criteria criteria = new Criteria();
            criteria.addOrder(periodBean.getFieldName(IAccountAlias.PERIOD_ID), false);
			Iterator iter = periodBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Period period = (Period)iter.next();
				SelectItem item = new SelectItem(period.getId(), period.getId());
				this.accountPeriods.add(item);
			}
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