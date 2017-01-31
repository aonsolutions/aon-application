package com.code.aon.accounting.event;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

        if (accountEntryDetail.getAccountEntry().isInvoiceEntry()) {
        	try {
            	IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            	Criteria criteria = new Criteria();
            	criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ID), accountEntryDetail.getId());
            	Projection prjAccountId = Projection.property(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID));
            	Object accountId = entryDetailBean.getUniqueResult(prjAccountId, criteria);
            	if (accountId != null) {
                	accountEntryDetail.setSavedAccountId((Integer)accountId);
            	}
        	} catch (ManagerBeanException ex) {
        		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
        	}
        }
    }

}
