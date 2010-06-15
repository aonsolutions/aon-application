package com.code.aon.account.bridge.util;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.accounting.Loan;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.supplier.Supplier;

public class AccountBridgeUtil {
	
	private AccountUtil accountUtil;
	
	private AccountUtil getAccountUtil() {
		if (accountUtil == null) {
			accountUtil = new AccountUtil(); 
		}
		return accountUtil;
	}

	public RegistryBank obtainRBank(String account) throws ManagerBeanException {
		try {
			IManagerBean rBankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(rBankAccountBean.getFieldName(IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_ACCOUNT_ID), account);
			Iterator<ITransferObject> iter = rBankAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((RegistryBankAccount)iter.next()).getRegistryBank();
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
		return null;
	}

	public Account obtainRBankAccount(RegistryBank rBank) throws ManagerBeanException {
		try {
			IManagerBean rBankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankAccountBean.getFieldName(IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_REGISTRY_BANK_ID), rBank.getId());
			Iterator<ITransferObject> iter = rBankAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryBankAccount rBankAccount = (RegistryBankAccount)iter.next();
				return rBankAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			account.setId(getAccountUtil().obtainNextAccountId(AccountConstants.BANK_ACCOUNT_PREFIX));
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
	
	public IAccount obtainIRegistryAccount(IRegistry iRegistry) throws ManagerBeanException {
		if (iRegistry instanceof Customer) {
			return obtainCustomerIAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Supplier) {
			return obtainSupplierIAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Creditor) {
			return obtainCreditorIAccount(iRegistry.getRegistry());
		}
		throw new ManagerBeanException("No se puede obtener la cuenta de un " + iRegistry); 
	}

	public IAccount getIRegistryAccount(IRegistry iRegistry) throws ManagerBeanException {
		if (iRegistry instanceof Customer) {
			return getCustomerIAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Supplier) {
			return getSupplierIAccount(iRegistry.getRegistry());
		} else if (iRegistry instanceof Creditor) {
			return getCreditorIAccount(iRegistry.getRegistry());
		}
		throw new ManagerBeanException("No se puede obtener la cuenta de un " + iRegistry); 
	}
	public Account getCustomerAccount(Registry registry) throws ManagerBeanException {
		IAccount customerAccount = getCustomerIAccount(registry);
		return customerAccount==null?null:customerAccount.getAccount();
	}
	public CustomerAccount getCustomerIAccount(Registry registry) throws ManagerBeanException {
		IManagerBean customerAccountBean = BeanManager.getManagerBean(CustomerAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerAccountBean.getFieldName(IAccountBridgeAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID), registry.getId());
		Iterator<ITransferObject> iter = customerAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			CustomerAccount customerAccount = (CustomerAccount)iter.next();
			return customerAccount;
		}
		return null;
	}
	public Account obtainCustomerAccount(Registry registry) throws ManagerBeanException {
		IAccount customerAccount = obtainCustomerIAccount(registry);
		return customerAccount==null?null:customerAccount.getAccount();
	}
	public CustomerAccount obtainCustomerIAccount(Registry registry) throws ManagerBeanException {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Customer customer = (Customer) customerBean.get(registry.getId());
			if (customer != null) {
				CustomerAccount customerAccount = getCustomerIAccount(registry);
				if (customerAccount == null) {
					IManagerBean customerAccountBean = BeanManager.getManagerBean(CustomerAccount.class);
					IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
					Account account = new Account();
					account.setId(getAccountUtil().obtainNextAccountId(AccountConstants.CUSTOMER_ACCOUNT_PREFIX));
					account.setDescription(registry.getFullName());
					account.setEntryEnabled(true);
					account.setAlias(registry.getAlias());
					account = (Account) accountBean.insert(account);
					customerAccount = new CustomerAccount();
					customerAccount.setAccount(account);
					customerAccount.setCustomer(customer);
					customerAccountBean.insert(customerAccount);
				}
				return customerAccount;
			}
			return null;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	public Account getSupplierAccount(Registry registry) throws ManagerBeanException {
		IAccount supplierAccount = getSupplierIAccount(registry);
		return supplierAccount==null?null:supplierAccount.getAccount();
	}
	public SupplierAccount getSupplierIAccount(Registry registry) throws ManagerBeanException {
		IManagerBean supplierAccountBean = BeanManager.getManagerBean(SupplierAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(supplierAccountBean.getFieldName(IAccountBridgeAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID), registry.getId());
		Iterator<ITransferObject> iter = supplierAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			SupplierAccount supplierAccount = (SupplierAccount)iter.next();
			return supplierAccount;
		}
		return null;
	}
	public Account obtainSupplierAccount(Registry registry) throws ManagerBeanException {
		IAccount supplierAccount = obtainSupplierIAccount(registry);
		return supplierAccount==null?null:supplierAccount.getAccount();
	}
	public SupplierAccount obtainSupplierIAccount(Registry registry) throws ManagerBeanException {
		try {
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Supplier supplier = (Supplier) supplierBean.get(registry.getId());
			if (supplier != null) {
				SupplierAccount supplierAccount = getSupplierIAccount(registry);
				if (supplierAccount == null) {
					IManagerBean supplierAccountBean = BeanManager.getManagerBean(SupplierAccount.class);
					IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
					Account account = new Account();
					account.setId(getAccountUtil().obtainNextAccountId(AccountConstants.SUPPLIER_ACCOUNT_PREFIX));
					account.setDescription(registry.getFullName());
					account.setEntryEnabled(true);
					account.setAlias(registry.getAlias());
					account = (Account) accountBean.insert(account);
					supplierAccount = new SupplierAccount();
					supplierAccount.setAccount(account);
					supplierAccount.setSupplier((Supplier) supplierBean.get(registry.getId()));
					supplierAccountBean.insert(supplierAccount);
				}
				return supplierAccount;
			}
			return null;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}
	
	public Account getCreditorAccount(Registry registry) throws ManagerBeanException {
		IAccount creditorAccount = getCreditorIAccount(registry);
		return creditorAccount==null?null:creditorAccount.getAccount();
	}
	public CreditorAccount getCreditorIAccount(Registry registry) throws ManagerBeanException {
		IManagerBean creditorAccountBean = BeanManager.getManagerBean(CreditorAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(creditorAccountBean.getFieldName(IAccountBridgeAlias.CREDITOR_ACCOUNT_CREDITOR_ID), registry.getId());
		Iterator<ITransferObject> iter = creditorAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			CreditorAccount creditorAccount = (CreditorAccount)iter.next();
			return creditorAccount;
		}
		return null;
	}
	public Account obtainCreditorAccount(Registry registry) throws ManagerBeanException {
		IAccount creditorAccount = obtainCreditorIAccount(registry);
		return creditorAccount==null?null:creditorAccount.getAccount();
	}
	public CreditorAccount obtainCreditorIAccount(Registry registry) throws ManagerBeanException {
		try {
			IManagerBean creditorBean = BeanManager.getManagerBean(Creditor.class);
			Creditor creditor = (Creditor) creditorBean.get(registry.getId());
			if (creditor != null) {
				CreditorAccount creditorAccount = getCreditorIAccount(registry);
				if (creditorAccount == null) {
					IManagerBean creditorAccountBean = BeanManager.getManagerBean(CreditorAccount.class);
					IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
					Account account = new Account();
					account.setId(getAccountUtil().obtainNextAccountId(AccountConstants.CREDITOR_ACCOUNT_PREFIX));
					account.setDescription(registry.getFullName());
					account.setEntryEnabled(true);
					account.setAlias(registry.getAlias());
					account = (Account) accountBean.insert(account);
					creditorAccount = new CreditorAccount();
					creditorAccount.setAccount(account);
					creditorAccount.setCreditor((Creditor) creditorBean.get(registry.getId()));
					creditorAccountBean.insert(creditorAccount);
				}
				return creditorAccount;
			}
			return null;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e );
		}
	}

	public Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		try {
			IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
			Iterator<ITransferObject> iter = loanAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				LoanAccount loanAccount = (LoanAccount)iter.next();
				return loanAccount.getAccount();
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Account account = new Account();
			String prefix = AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX;
			account.setId(getAccountUtil().obtainNextAccountId(prefix));
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

}