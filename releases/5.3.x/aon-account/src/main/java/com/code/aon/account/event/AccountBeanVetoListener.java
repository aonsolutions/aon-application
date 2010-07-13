package com.code.aon.account.event;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

public class AccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String ERROR_MSG = "La cuenta debe tener una longitud de 1, 2, 3, 4 ó 9 caracteres.";
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	try {
    		Long.parseLong(to.getId());
    	} catch (NumberFormatException e) {
			throw new ManagerBeanVetoListenerException("Todos los caracteres de la cuenta deben ser numéricos.");
    	}
    	checkValidLength(to);
    	int level = (to.getId().length() > 4) ? 5 : to.getId().length();
    	to.setLevel(level);
    	to.setEntryEnabled(level==5);
    	checkParent(to);
    }
	
    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	checkValidLength(to);
    	checkParent(to);
	}

	private void checkValidLength(Account account) throws ManagerBeanVetoListenerException {
		int length = account.getId().length();
		if (length != 1 && length != 2 && length != 3 && length != 4 && length != 9 ) {
			throw new ManagerBeanVetoListenerException(ERROR_MSG);
		}
	}
	@SuppressWarnings("unchecked")
	private void checkParent(Account account) throws ManagerBeanVetoListenerException {
		if (account.getLevel()>1) {
			try {
				int parentLevel = account.getLevel() - 1;
				String parentId = account.getId().substring(0, parentLevel);
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID),parentId);
				Iterator iterator = accountBean.getList(criteria).iterator();
				if (!iterator.hasNext()) {
					throw new ManagerBeanVetoListenerException("Imposible crear cuenta. No existe cuenta correspondiente de nivel inferior.");
			} 
			} catch (ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException("Imposible crear cuenta. " + e.getMessage());
			}
		}
	}

}
