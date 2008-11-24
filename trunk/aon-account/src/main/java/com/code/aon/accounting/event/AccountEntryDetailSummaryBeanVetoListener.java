package com.code.aon.accounting.event;

import java.util.Iterator;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailSummaryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    @SuppressWarnings("unchecked")
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail accountEntryDetail = (AccountEntryDetail)evt.getTo();
        try {
            IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ID), accountEntryDetail.getId());
            Iterator iterator = accountEntryDetailBean.getList(criteria).iterator();
            if (iterator.hasNext()) {
            	accountEntryDetail = (AccountEntryDetail)iterator.next();
                AccountSummaryManager.modifyAccountSummary(accountEntryDetail, -1);
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
    }

}
