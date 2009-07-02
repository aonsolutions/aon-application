package com.code.aon.accounting.event;

import java.util.Iterator;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntrySummaryBeanListener extends ManagerBeanListenerAdapter {

    @Override
    @SuppressWarnings("unchecked")
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
        AccountEntry accountEntry = (AccountEntry)evt.getTo();
		if (accountEntry.isDateDirty()) {
	        IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	        Criteria criteria = new Criteria();
	        criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
	        Iterator iterator = accountEntryDetailBean.getList(criteria).iterator();
	        while (iterator.hasNext()) {
	        	AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iterator.next();
	            AccountSummaryManager.modifyAccountSummary(accountEntryDetail, 1);
	        }
	    }
    }

}
