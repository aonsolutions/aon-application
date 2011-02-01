package com.code.aon.account.bridge.event;


import java.util.List;

import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

public class RegistryBankAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		RegistryBankAccount registryBankAccount = (RegistryBankAccount) evt.getTo();
		checkIfRegistryBankAssigned(registryBankAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		RegistryBankAccount registryBank = (RegistryBankAccount) evt.getTo();
		checkIfRegistryBankAssigned(registryBank);
	}

	private void checkIfRegistryBankAssigned(RegistryBankAccount registryBankAccount) throws ManagerBeanVetoListenerException{
		try {
			if (registryBankAccount.getRegistryBank() == null) {
				throw new ManagerBeanVetoListenerException("El banco es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Integer id = registryBankAccount.getRegistryBank().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_REGISTRY_BANK_ID);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( registryBankAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, registryBankAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				RegistryBankAccount dup = (RegistryBankAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El banco ");
				sb.append(registryBankAccount.getAccountDescription());
				sb.append(" ya está enlazado con la cuenta ");
				sb.append(dup.getAccount().getId());
				sb.append(" ");
				sb.append(dup.getAccount().getDescription());
				sb.append(".");
				throw new ManagerBeanVetoListenerException(sb.toString()); 
			}
			
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e); 
		}
		
		
		
	}

}
