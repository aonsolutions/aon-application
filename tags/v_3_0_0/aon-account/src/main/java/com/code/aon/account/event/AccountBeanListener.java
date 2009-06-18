package com.code.aon.account.event;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class AccountBeanListener extends ManagerBeanListenerAdapter {


	@Override
	@SuppressWarnings("unchecked")
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	Account to = (Account)evt.getTo();
    	try {
	    	IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
	    	Criteria criteria = null;
	    	for(int i=to.getId().length()-1; i>0; i--) {
	    		if (criteria == null) {
	    			criteria = new Criteria();
	    		}
	    		criteria.addOrExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), to.getId().substring(0, i));
	    	}

	    	if (criteria != null) {
	    		criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), false);
		    	Iterator iterator = accountBean.getList(criteria).iterator();
		    	if (iterator.hasNext()) {
					Account account = (Account)iterator.next();
					account.setEntryEnabled(false);
					accountBean.update(account);
		    	}
	    	}
    	} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
    }

    @Override
	@SuppressWarnings("unchecked")
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	Account to = (Account)evt.getTo();
    	try {
	    	IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
	    	Criteria criteria = null;
	    	for(int i=to.getId().length()-1; i>0; i--) {
	    		if (criteria == null) {
	    			criteria = new Criteria();
	    		}
	    		criteria.addOrExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), to.getId().substring(0, i));
	    	}

	    	if (criteria != null) {
	    		criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), false);
		    	Iterator iterator = accountBean.getList(criteria).iterator();
		    	if (iterator.hasNext()) {
		    		Account account = (Account)iterator.next();
		    		criteria = new Criteria();
		    		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), account.getId() + "*");
		    		criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), false);
			    	Iterator iter = accountBean.getList(criteria).iterator();
			    	if (iter.hasNext()) {
			    		account = (Account)iter.next();
				    	account.setEntryEnabled(true);
				    	accountBean.update(account);
			    	}
		    	}
	    	}
    	} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
    }

}