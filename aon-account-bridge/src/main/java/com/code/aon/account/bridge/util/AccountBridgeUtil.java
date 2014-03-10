package com.code.aon.account.bridge.util;

import java.io.Serializable;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.accounting.Loan;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountBridgeUtil implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private AccountUtil accountUtil;
	
	private AccountUtil getAccountUtil() {
		if (accountUtil == null) {
			accountUtil = new AccountUtil(); 
		}
		return accountUtil;
	}

	public Account getCustomerAccount(Registry registry) throws ManagerBeanException {
		Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(registry.getId());
		return (customer!=null) ? customer.getAccount() : null;
	}
	public Account obtainCustomerAccount(Registry registry) throws ManagerBeanException {
		Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(registry.getId());
		return (customer!=null) ? (customer.getAccount()!=null ? customer.getAccount() : obtainNewCustomerAccount(customer)) : null;
	}
	public Account obtainNewCustomerAccount(Customer customer) throws ManagerBeanException {
		try {
			Account account = new Account();
			account.setCode(getAccountUtil().obtainNextAccountId(AccountConstants.CUSTOMER_ACCOUNT_PREFIX));
			account.setDescription(customer.getRegistry().getFullName());
			account.setAlias(customer.getRegistry().getAlias());
			account.setEntryEnabled(true);
			account = (Account)BeanManager.getManagerBean(Account.class).insert(account);

			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			customer.setAccount(account);
			customerBean.restoreNullSubPOJOs(customer);
			customer = (Customer)customerBean.update(customer);

			return account;
		} catch (ExpressionException ex) {
			throw new ManagerBeanException(ex.getMessage(), ex);
		}
	}
	
	public Account getSupplierAccount(Registry registry) throws ManagerBeanException {
		Supplier supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(registry.getId());
		return (supplier!=null) ? supplier.getAccount() : null;
	}
	public Account obtainSupplierAccount(Registry registry) throws ManagerBeanException {
		Supplier supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(registry.getId());
		return (supplier!=null) ? (supplier.getAccount()!=null ? supplier.getAccount() : obtainNewSupplierAccount(supplier)) : null;
	}
	public Account obtainNewSupplierAccount(Supplier supplier) throws ManagerBeanException {
		try {
			Account account = new Account();
			account.setCode(getAccountUtil().obtainNextAccountId(AccountConstants.SUPPLIER_ACCOUNT_PREFIX));
			account.setDescription(supplier.getRegistry().getFullName());
			account.setAlias(supplier.getRegistry().getAlias());
			account.setEntryEnabled(true);
			account = (Account)BeanManager.getManagerBean(Account.class).insert(account);

			supplier.setAccount(account);
			supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).update(supplier);

			return account;
		} catch (ExpressionException ex) {
			throw new ManagerBeanException(ex.getMessage(), ex);
		}
	}
	
	public Account getCreditorAccount(Registry registry) throws ManagerBeanException {
		Creditor creditor = (Creditor)BeanManager.getManagerBean(Creditor.class).get(registry.getId());
		return (creditor!=null) ? creditor.getAccount() : null;
	}
	public Account obtainCreditorAccount(Registry registry) throws ManagerBeanException {
		Creditor creditor = (Creditor)BeanManager.getManagerBean(Creditor.class).get(registry.getId());
		return (creditor!=null) ? (creditor.getAccount()!=null ? creditor.getAccount() : obtainNewCreditorAccount(creditor)) : null;
	}
	public Account obtainNewCreditorAccount(Creditor creditor) throws ManagerBeanException {
		try {
			Account account = new Account();
			account.setCode(getAccountUtil().obtainNextAccountId(AccountConstants.CREDITOR_ACCOUNT_PREFIX));
			account.setDescription(creditor.getRegistry().getFullName());
			account.setAlias(creditor.getRegistry().getAlias());
			account.setEntryEnabled(true);
			account = (Account)BeanManager.getManagerBean(Account.class).insert(account);

			creditor.setAccount(account);
			creditor = (Creditor)BeanManager.getManagerBean(Creditor.class).update(creditor);

			return account;
		} catch (ExpressionException ex) {
			throw new ManagerBeanException(ex.getMessage(), ex);
		}
	}

	public Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		return (loan.getAccount()!=null && loan.getAccount().getId()!=null) ? loan.getAccount() : obtainNewLoanAccount(loan);
	}
	public Account obtainNewLoanAccount(Loan loan) throws ManagerBeanException {
		try {
			Account account = new Account();
			account.setCode(getAccountUtil().obtainNextAccountId(AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX));
			account.setDescription(loan.getDescription());
			account.setAlias(account.getCode());
			account.setEntryEnabled(true);
			account = (Account)BeanManager.getManagerBean(Account.class).insert(account);

			loan.setAccount(account);
			loan = (Loan)BeanManager.getManagerBean(Loan.class).update(loan);

			return account;
		} catch (ExpressionException ex) {
			throw new ManagerBeanException(ex.getMessage(), ex);
		}
	}

	public Account obtainRBankAccount(RegistryBank rBank) throws ManagerBeanException {
		return (rBank.getAccount()!=null && rBank.getAccount().getId()!=null) ? rBank.getAccount() : obtainNewRBankAccount(rBank);
	}
	public Account obtainNewRBankAccount(RegistryBank rBank) throws ManagerBeanException {
		try {
			Account account = new Account();
			account.setCode(getAccountUtil().obtainNextAccountId(AccountConstants.BANK_ACCOUNT_PREFIX));
			account.setDescription(rBank.getFullName());
			account.setAlias(rBank.getAlias());
			account.setEntryEnabled(true);
			account = (Account)BeanManager.getManagerBean(Account.class).insert(account);

			rBank.setAccount(account);
			rBank = (RegistryBank)BeanManager.getManagerBean(RegistryBank.class).update(rBank);

			return account;
		} catch (ExpressionException ex) {
			throw new ManagerBeanException(ex.getMessage(), ex);
		}
	}
	
	public RegistryBank obtainRBank(Account account) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ACCOUNT_ID), account.getId());
		for (ITransferObject ito : rBankBean.getList(criteria)) {
			return (RegistryBank)ito;
		}
		return null;
	}

	public PayMethodTypeDetail obtainPayMethodTypeDetail(Account account) throws ManagerBeanException {
		IManagerBean pmTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(pmTypeDetailBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE_DETAIL_ACCOUNT_ID), account.getId());
		for (ITransferObject ito : pmTypeDetailBean.getList(criteria)) {
			return (PayMethodTypeDetail)ito;
		}
		return null;
	}

}