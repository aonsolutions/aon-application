package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail accountEntryDetail = (AccountEntryDetail)evt.getTo();
        accountEntryDetail.setDebit(CommonUtil.round(accountEntryDetail.getDebit()));
        accountEntryDetail.setCredit(CommonUtil.round(accountEntryDetail.getCredit()));
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail accountEntryDetail = (AccountEntryDetail)evt.getTo();
        accountEntryDetail.setDebit(CommonUtil.round(accountEntryDetail.getDebit()));
        accountEntryDetail.setCredit(CommonUtil.round(accountEntryDetail.getCredit()));
    }

}
