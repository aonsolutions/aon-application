package com.code.aon.accounting.event;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountBudget;
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
public class AccountBudgetBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		isAccountEntryEnabled(evt);
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		isAccountEntryEnabled(evt);
    }

	private void isAccountEntryEnabled(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		AccountBudget to = (AccountBudget)evt.getTo();
		String id = to.getAccount().getId();
		
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
	        Criteria criteria = new Criteria();
            criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), id);
            Account account = (Account)accountBean.getList(criteria).get(0);
            if(!account.isEntryEnabled())
            	throw new ManagerBeanVetoListenerException("La cuenta no permite apuntes ");
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
	}

}
