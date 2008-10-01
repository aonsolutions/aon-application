package com.code.aon.account.event;

import java.util.Iterator;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.dao.IAccountAlias;
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
        IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
        Iterator iterator = accountEntryDetailBean.getList(criteria).iterator();
        while (iterator.hasNext()) {
        	AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iterator.next();
            AccountSummaryManager.modifyAccountSummary(accountEntryDetail, 1);
        }
    }

}
