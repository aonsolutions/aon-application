package com.code.aon.account.bridge.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.account.Account;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.Leasing;
import com.code.aon.account.Loan;
import com.code.aon.account.Period;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;

public class AccountUtil {
	
	private static final Logger LOGGER = Logger.getLogger(AccountUtil.class.getName());

	@SuppressWarnings("unchecked")
	public static Account obtainRBankAccount(RegistryBank rBank) {
		try {
			IManagerBean rBankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankAccountBean.getFieldName(IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_REGISTRY_BANK_ID), rBank.getId());
			Iterator iter = rBankAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryBankAccount rBankAccount = (RegistryBankAccount)iter.next();
				return rBankAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.BANK_ACCOUNT_PREFIX));
			account.setDescription(rBank.getBank().getName() + " " + rBank.getBankAccount());
			account.setEntryEnabled(true);
			account.setAlias(account.getId());
			account = (Account) accountBean.insert(account);
			RegistryBankAccount rBankAccount = new RegistryBankAccount();
			rBankAccount.setAccount(account);
			rBankAccount.setRegistryBank(rBank);
			rBankAccountBean.insert(rBankAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registryBankAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registryBankAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCustomerAccount(Registry registry) {
		try {
			IManagerBean customerAccountBean = BeanManager.getManagerBean(CustomerAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerAccountBean.getFieldName(IAccountBridgeAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID), registry.getId());
			Iterator iter = customerAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				CustomerAccount customerAccount = (CustomerAccount)iter.next();
				return customerAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.CUSTOMER_ACCOUNT_PREFIX));
			account.setDescription((registry.getName()!= null?registry.getName() + " ":"") + (registry.getSurname()!=null?registry.getSurname():""));
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setAccount(account);
			customerAccount.setCustomer(obtainCustomer(registry.getId()));
			customerAccountBean.insert(customerAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining customerAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining customerAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainSupplierAccount(Registry registry) {
		try {
			IManagerBean supplierAccountBean = BeanManager.getManagerBean(SupplierAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierAccountBean.getFieldName(IAccountBridgeAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID), registry.getId());
			Iterator iter = supplierAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				SupplierAccount supplierAccount = (SupplierAccount)iter.next();
				return supplierAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.SUPPLIER_ACCOUNT_PREFIX));
			account.setDescription((registry.getName()!=null?registry.getName() + " ":"") + (registry.getSurname()!=null?registry.getSurname():""));
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			SupplierAccount supplierAccount = new SupplierAccount();
			supplierAccount.setAccount(account);
			supplierAccount.setSupplier(obtainSupplier(registry.getId()));
			supplierAccountBean.insert(supplierAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining supplierAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining supplierAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCreditorAccount(Registry registry) {
		try {
			IManagerBean creditorAccountBean = BeanManager.getManagerBean(CreditorAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(creditorAccountBean.getFieldName(IAccountBridgeAlias.CREDITOR_ACCOUNT_CREDITOR_ID), registry.getId());
			Iterator iter = creditorAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				CreditorAccount creditorAccount = (CreditorAccount)iter.next();
				return creditorAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.CREDITOR_ACCOUNT_PREFIX));
			account.setDescription((registry.getName()!=null?registry.getName() + " ":"") + (registry.getSurname()!=null?registry.getSurname():""));
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			CreditorAccount creditorAccount = new CreditorAccount();
			creditorAccount.setAccount(account);
			creditorAccount.setCreditor(obtainCreditor(registry.getId()));
			creditorAccountBean.insert(creditorAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining creditorAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining creditorAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainLeasingAccount(Leasing leasing) {
		try {
			IManagerBean leasingAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(leasingAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_LEASING_ID), leasing.getId());
			Iterator iter = leasingAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				LeasingAccount leasingAccount = (LeasingAccount)iter.next();
				return leasingAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.LEASING_ACCOUNT_PREFIX));
			account.setDescription(leasing.getDescription());
			account.setEntryEnabled(true);
			account.setAlias(account.getId());
			account = (Account) accountBean.insert(account);
			LeasingAccount leasingAccount = new LeasingAccount();
			leasingAccount.setAccount(account);
			leasingAccount.setLeasing(leasing);
			leasingAccountBean.insert(leasingAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining LeasingAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining LeasingAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainLoanAccount(Loan loan) {
		try {
			IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
			Iterator iter = loanAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				LoanAccount loanAccount = (LoanAccount)iter.next();
				return loanAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(obtainNextAccountId(AccountConstants.LOAN_ACCOUNT_PREFIX));
			account.setDescription(loan.getDescription());
			account.setEntryEnabled(true);
			account.setAlias(account.getId());
			account = (Account) accountBean.insert(account);
			LoanAccount loanAccount = new LoanAccount();
			loanAccount.setAccount(account);
			loanAccount.setLoan(loan);
			loanAccountBean.insert(loanAccount);
			return account;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining loanAccount", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining loanAccount", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCashAccount() throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), ((Account)obtainDefaultAccount(DefaultAccounts.CASH_ACCOUNT)).getId());
		List list = accountBean.getList(criteria);
		if(list.size() > 0){
			return (Account)list.iterator().next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainDefaultAccount(String defaultAccountName) throws ManagerBeanException {
		IManagerBean accAppParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accAppParamBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), defaultAccountName);
		Iterator iter = accAppParamBean.getList(criteria).iterator();
		if(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			return obtainAccount(param.getValue());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainAccount(String account) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), account);
		List list = accountBean.getList(criteria);
		if(list.size() > 0){
			return (Account)list.iterator().next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Period obtainPeriod(Date date) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Period)iter.next();
		}
		throw new ManagerBeanException("Not Period defined for current Date");

	}
	
	@SuppressWarnings("unchecked")
	private static Customer obtainCustomer(Integer id) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Customer)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining customer with id= " + id, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Supplier obtainSupplier(Integer id) {
		try {
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierBean.getFieldName(ISupplierAlias.SUPPLIER_ID), id);
			Iterator iter = supplierBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Supplier)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining supplier with id= " + id, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Creditor obtainCreditor(Integer id) {
		try {
			IManagerBean creditorBean = BeanManager.getManagerBean(Creditor.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(creditorBean.getFieldName(IFinanceAlias.CREDITOR_ID), id);
			Iterator iter = creditorBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Creditor)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining creditor with id= " + id, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static String obtainNextAccountId(String prefix) throws ManagerBeanException, ExpressionException{
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), fillprefix(prefix));
		criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), false);
		Iterator iter = accountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Account account = (Account)iter.next();
			return new Long(Long.parseLong(account.getId()) + 1).toString();
		}
		return zerofill(prefix); 
	}
	
	private static String zerofill(String prefix) {
		String string = "1";
		for(int i=0;i< 9 - (prefix.length() + 1); i++){
			string = "0" + string;
		}
		return prefix + string;
	}
	
	private static String fillprefix(String prefix) {
		String string = "";
		for(int i=0; i< 9 - (prefix.length());i++){
			string = string + "?";
		}
		return prefix + string;
	}
}