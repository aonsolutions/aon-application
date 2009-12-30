package com.code.aon.account.event;

import java.util.Iterator;

import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.dao.IAccountAlias;
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
public class AccountSummaryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail entryDetail = (AccountEntryDetail)evt.getTo();
        try {
            IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ID), entryDetail.getId());
            Iterator iterator = entryDetailBean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                entryDetail = (AccountEntryDetail)iterator.next();
                AccountSummaryManager.modifyAccountSummary(entryDetail, -1);
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
    }

}
