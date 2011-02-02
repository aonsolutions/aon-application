package com.code.aon.account.bridge.event;

import java.util.List;

import com.code.aon.account.bridge.CreditorAccount;
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

public class CreditorAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		CreditorAccount creditorAccount = (CreditorAccount) evt.getTo();
		checkIfCreditorAssigned(creditorAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		CreditorAccount creditorAccount = (CreditorAccount) evt.getTo();
		checkIfCreditorAssigned(creditorAccount);
	}

	private void checkIfCreditorAssigned(CreditorAccount creditorAccount) throws ManagerBeanVetoListenerException{
		try {
			if (creditorAccount.getCreditor() == null) {
				throw new ManagerBeanVetoListenerException("El acreedor es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(CreditorAccount.class);
			Integer id = creditorAccount.getCreditor().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.CREDITOR_ACCOUNT_CREDITOR_ID);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( creditorAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.CREDITOR_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, creditorAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				CreditorAccount dup = (CreditorAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El acreedor ");
				sb.append(creditorAccount.getAccountDescription());
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
