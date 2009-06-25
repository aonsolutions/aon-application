package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailSummaryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail accountEntryDetail = (AccountEntryDetail)evt.getTo();
        try {
            IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            accountEntryDetail = (AccountEntryDetail) accountEntryDetailBean.get(accountEntryDetail.getId());
            if (accountEntryDetail != null) {
                AccountSummaryManager.modifyAccountSummary(accountEntryDetail, -1);
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
    }

}
