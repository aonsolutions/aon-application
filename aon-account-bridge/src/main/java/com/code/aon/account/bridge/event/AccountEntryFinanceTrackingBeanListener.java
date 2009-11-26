package com.code.aon.account.bridge.event;

import java.util.Iterator;

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;

public class AccountEntryFinanceTrackingBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		AccountEntryFinanceTracking aeft = (AccountEntryFinanceTracking) evt.getTo();
		try {
			removeAccountEntryDetails(aeft.getAccountEntry());
			removeAccountEntry(aeft.getAccountEntry());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry
				.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			AccountEntryDetail accEntryDetail = (AccountEntryDetail) iter.next();
			accountEntryDetailBean.remove(accEntryDetail);
		}
	}

	private void removeAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}
}
