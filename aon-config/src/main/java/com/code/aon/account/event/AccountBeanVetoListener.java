package com.code.aon.account.event;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String ERROR_MSG = "La cuenta debe tener una longitud de 1, 2, 3, 4 ó 9 caracteres.";
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	try {
    		Long.parseLong(to.getCode());
    	} catch (NumberFormatException e) {
			throw new ManagerBeanVetoListenerException("Todos los caracteres de la cuenta deben ser numéricos.");
    	}
    	checkValidLength(to);
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE),to.getCode());
			List<ITransferObject> list =  accountBean.getList(criteria);
			Iterator<ITransferObject> iterator = list.iterator();
			if (iterator.hasNext()) {
				Account duplicate = (Account) iterator.next();
				throw new ManagerBeanVetoListenerException("No se puede crear la cuenta (" + to.getFullDescription() 
						+ ") porque ya existe una cuenta con el mismo CCC (" + duplicate.getFullDescription() + ")");	
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No se pudo chequear la existencia de la cuenta contable.");
		}
    	
    	int level = (to.getCode().length() > 4) ? 5 : to.getCode().length();
    	to.setLevel(level);
    	to.setEntryEnabled(level==5);
    	checkParent(to);
    	if (StringUtils.isBlank( to.getCostCenter())) {
    		to.setCostCenter(null);
    	}
    }
	
    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	checkValidLength(to);
    	checkParent(to);
    	if (StringUtils.isBlank( to.getCostCenter())) {
    		to.setCostCenter(null);
    	}
	}

	private void checkValidLength(Account account) throws ManagerBeanVetoListenerException {
		int length = account.getCode().length();
		if (length != 1 && length != 2 && length != 3 && length != 4 && length != 9 ) {
			throw new ManagerBeanVetoListenerException(ERROR_MSG + "["+account.getCode()+"]");
		}
	}
	private void checkParent(Account account) throws ManagerBeanVetoListenerException {
		if (account.getLevel()>1) {
			try {
				int parentLevel = account.getLevel() - 1;
				String parentCode = account.getCode().substring(0, parentLevel);
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE),parentCode);
				Iterator<ITransferObject> iterator = accountBean.getList(criteria).iterator();
				if (!iterator.hasNext()) {
					throw new ManagerBeanVetoListenerException("Imposible crear cuenta. No existe cuenta correspondiente de nivel inferior " + "["+account.getCode()+"]");
			} 
			} catch (ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException("Imposible crear cuenta. " + "["+account.getCode()+"] " + e.getMessage());
			}
		}
	}

}
