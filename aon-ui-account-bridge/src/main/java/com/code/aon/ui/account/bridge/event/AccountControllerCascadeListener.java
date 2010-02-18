package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerCascadeListener extends ControllerAdapter {

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Account account = (Account)event.getController().getTo();
			if(!account.isEntryEnabled()){
				throw new ControllerListenerException("Imposible borrar cuenta. La cuenta está extendida.");
			}else if(isSystemAccount(account)){
				throw new ControllerListenerException("Imposible borrar cuenta. Cuenta necesaria para el sistema.");
			}else if(obtainAccountEntryDetailCount(account) != 0){
				throw new ControllerListenerException("Imposible borrar cuenta. Hay apuntes contables asociados.");
			}else if(!accountSummaryRemovable(account)){
				throw new ControllerListenerException("Imposible borrar cuenta. Hay apuntes contables asociados.");
			}else {
				removeAccountRelatedAccountSummary(account);
				removeRelatedLinkTable(account);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private int obtainAccountEntryDetailCount(Account account) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), account.getId());
		return accountEntryDetailBean.getCount(criteria); 
	}
	
	private boolean accountSummaryRemovable(Account account) throws ManagerBeanException {
		IManagerBean accountSummaryBean = BeanManager.getManagerBean(AccountSummary.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ACCOUNT_ID), account.getId());
		Expression creditExpression = ExpressionUtilities.getGreaterThanExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_CREDIT), new Double(0.0));
		Expression debitExpression = ExpressionUtilities.getGreaterThanExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_DEBIT), new Double(0.0));
		criteria.addExpression(ExpressionUtilities.getOrExpression(creditExpression, debitExpression));
		return accountSummaryBean.getCount(criteria) == 0;
	}
	
	@SuppressWarnings("unchecked")
	private void removeAccountRelatedAccountSummary(Account account) throws ManagerBeanException {
		IManagerBean accountSummaryBean = BeanManager.getManagerBean(AccountSummary.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ACCOUNT_ID), account.getId());
		Iterator iter = accountSummaryBean.getList(criteria).iterator();
		while(iter.hasNext()){
			accountSummaryBean.remove((AccountSummary)iter.next());
		}
	}
	
	private void removeRelatedLinkTable(Account account) throws ManagerBeanException {
		if(account.getId().length() >= 5){
			String prefix = account.getId().substring(0, 5);
			if(prefix.equals(AccountConstants.BANK_ACCOUNT_PREFIX)){
				IManagerBean bankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
				deleteLink(bankAccountBean, IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_ACCOUNT_ID, account.getId());
			}else if(prefix.equals(AccountConstants.CUSTOMER_ACCOUNT_PREFIX)){
				IManagerBean customerAccountBean = BeanManager.getManagerBean(CustomerAccount.class);
				deleteLink(customerAccountBean, IAccountBridgeAlias.CUSTOMER_ACCOUNT_ACCOUNT_ID, account.getId());
			}else if(prefix.equals(AccountConstants.SUPPLIER_ACCOUNT_PREFIX)){
				IManagerBean supplierAccountBean = BeanManager.getManagerBean(SupplierAccount.class);
				deleteLink(supplierAccountBean, IAccountBridgeAlias.SUPPLIER_ACCOUNT_ACCOUNT_ID, account.getId());
			}else if(prefix.equals(AccountConstants.CREDITOR_ACCOUNT_PREFIX)){
				IManagerBean creditorAccountBean = BeanManager.getManagerBean(CreditorAccount.class);
				deleteLink(creditorAccountBean, IAccountBridgeAlias.CREDITOR_ACCOUNT_ACCOUNT_ID, account.getId());
			} 
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_ACCOUNT_ID), account.getId());
			List<?> list = productAccountBean.getList(criteria);
			Iterator<?> iter = list.iterator();
			while (iter.hasNext()) {
				productAccountBean.remove( (ITransferObject) iter.next() );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteLink(IManagerBean bean, String alias, String accountId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(alias), accountId);
		Iterator iter = bean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			ITransferObject to = (ITransferObject) iter.next();
			bean.remove(to);
		}
	}
	
	private boolean isSystemAccount(Account account) {
		for(String systemAccount:AccountConstants.getSystemAccounts()){
			if(systemAccount.equals(account.getId())){
				return true;
			}
		}
		return false;
	}
}