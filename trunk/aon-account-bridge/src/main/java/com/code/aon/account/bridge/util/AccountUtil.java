package com.code.aon.account.bridge.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;

public class AccountUtil {

	@SuppressWarnings("unchecked")
	public static RegistryBank obtainRBank(String account) throws ManagerBeanException {
		try {
			IManagerBean rBankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(rBankAccountBean.getFieldName(IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_ACCOUNT_ID), account);
			Iterator iter = rBankAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((RegistryBankAccount)iter.next()).getRegistryBank();
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Account obtainRBankAccount(RegistryBank rBank) throws ManagerBeanException {
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
			account.setDescription(rBank.getFullName());
			account.setEntryEnabled(true);
			account.setAlias(account.getId());
			account = (Account) accountBean.insert(account);
			RegistryBankAccount rBankAccount = new RegistryBankAccount();
			rBankAccount.setAccount(account);
			rBankAccount.setRegistryBank(rBank);
			rBankAccountBean.insert(rBankAccount);
			return account;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCustomerAccount(Registry registry) throws ManagerBeanException {
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
			account.setDescription(registry.getFullName());
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setAccount(account);
			customerAccount.setCustomer(obtainCustomer(registry.getId()));
			customerAccountBean.insert(customerAccount);
			return account;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	public static Account obtainIRegistryAccount(IRegistry iRegistry) throws ManagerBeanException {
		if (iRegistry instanceof Customer) {
			return obtainCustomerAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Supplier) {
			return obtainSupplierAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Creditor) {
			return obtainCreditorAccount(iRegistry.getRegistry());
		}
		throw new ManagerBeanException("No se puede obtener la cuenta de un " + iRegistry); 
	}
	@SuppressWarnings("unchecked")
	public static Account obtainSupplierAccount(Registry registry) throws ManagerBeanException {
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
			account.setDescription(registry.getFullName());
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			SupplierAccount supplierAccount = new SupplierAccount();
			supplierAccount.setAccount(account);
			supplierAccount.setSupplier(obtainSupplier(registry.getId()));
			supplierAccountBean.insert(supplierAccount);
			return account;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCreditorAccount(Registry registry) throws ManagerBeanException {
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
			account.setDescription(registry.getFullName());
			account.setEntryEnabled(true);
			account.setAlias(registry.getAlias());
			account = (Account) accountBean.insert(account);
			CreditorAccount creditorAccount = new CreditorAccount();
			creditorAccount.setAccount(account);
			creditorAccount.setCreditor(obtainCreditor(registry.getId()));
			creditorAccountBean.insert(creditorAccount);
			return account;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainLeasingAccount(Leasing leasing) throws ManagerBeanException {
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
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
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
			
			int term = 0; 
			try {
				term = Integer.parseInt(loan.getTerm());
			} catch (NumberFormatException e) {
				 
			}
			String prefix = (term>12)?AccountConstants.LONG_TERM_LOAN_ACCOUNT_PREFIX:AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX; 
			account.setId(obtainNextAccountId(prefix));
			account.setDescription(loan.getDescription());
			account.setEntryEnabled(true);
			account.setAlias(account.getId());
			account = (Account) accountBean.insert(account);
			LoanAccount loanAccount = new LoanAccount();
			loanAccount.setAccount(account);
			loanAccount.setLoan(loan);
			loanAccountBean.insert(loanAccount);
			return account;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Account obtainCashAccount() throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), obtainDefaultAccount(DefaultAccounts.CASH_ACCOUNT).getId());
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
	@Deprecated  //usar ImanagerBean.get(Serializable) 
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
		criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Period)iter.next();
		} 
		Calendar initiation = new GregorianCalendar();
		initiation.setTime(date);
		initiation.set(Calendar.DAY_OF_MONTH, 1);
		initiation.set(Calendar.MONTH, 0);
		Calendar deadline = new GregorianCalendar();
		deadline.setTime(date);
		deadline.set(Calendar.DAY_OF_MONTH, 31);
		deadline.set(Calendar.MONTH, 11);

		Period period = new Period();
		period.setId(Integer.toString(initiation.get(Calendar.YEAR)));
		period.setInitiationDate(initiation.getTime());
		period.setDeadline(deadline.getTime());
		return (Period)periodBean.insert(period);
	}
	
	@SuppressWarnings("unchecked")
	private static Customer obtainCustomer(Integer id) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
		Iterator iter = customerBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Supplier obtainSupplier(Integer id) throws ManagerBeanException {
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(supplierBean.getFieldName(ISupplierAlias.SUPPLIER_ID), id);
		Iterator iter = supplierBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Supplier)iter.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Creditor obtainCreditor(Integer id) throws ManagerBeanException {
		IManagerBean creditorBean = BeanManager.getManagerBean(Creditor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(creditorBean.getFieldName(IFinanceAlias.CREDITOR_ID), id);
		Iterator iter = creditorBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Creditor)iter.next();
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