package com.code.aon.account.event;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.Domain;
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
    	checkValidCode(to);
    	
    	
    	int level = (to.getCode().length() > 4) ? 5 : to.getCode().length();
    	to.setLevel(level);
    	to.setEntryEnabled(level==5);
    	checkParent(to);
    	if (StringUtils.isBlank( to.getCostCenter())) {
    		to.setCostCenter(null);
    	}
    }
	
    private void checkValidCode(Account to) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE),to.getCode());
			List<ITransferObject> list =  accountBean.getList(criteria);
			if (list != null && list.size() > 0) {
				Account duplicate = (Account) list.get(0);
				throw new ManagerBeanVetoListenerException("No se puede crear la cuenta (" + to.getFullDescription() 
						+ ") porque ya existe una cuenta con el mismo código (" + duplicate.getFullDescription() + ")");	
			}
			
			int currentDomain = DomainManager.getCurrentDomain();
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) domainBean.get(currentDomain);
			if (domain == null) {
				throw new ManagerBeanVetoListenerException("No se puede crear la cuenta (" + to.getFullDescription() 
						+ ") porque ya es imposible identificar el dominio en curso.");	
			}
			// Si estamos grabando una cuenta en un dominio padre, se chequea que no exista el 
			// código en ningún dominio hijo con la propiedad "heridity" habilitada.
			if ( domain.isDomainManagement() ) {
				Criteria c = new Criteria();
				c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), currentDomain);
				c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_ENABLE_HEREDITY), true);
				List<ITransferObject> domains = domainBean.getList(c);
				Criteria childCriteria;
				for (ITransferObject d : domains) {
					Domain child = (Domain) d;
					childCriteria = new Criteria();
					childCriteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_DOMAIN),child.getId());
					childCriteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE),to.getCode());
					childCriteria.setSkipDomainFilter(true);
					List<ITransferObject> accounts =  accountBean.getList(childCriteria);
					if (accounts != null && accounts.size() > 0) {
						Account duplicate = (Account) accounts.get(0);
						throw new ManagerBeanVetoListenerException("No se puede crear la cuenta (" + to.getFullDescription() 
								+ ") porque ya existe una cuenta con el mismo código "
								+ " en el dominio '"+child.getName()+" " + child.getDescription() +"'");	
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No se pudo chequear la existencia de la cuenta contable.");
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
