package com.code.aon.account.bridge.event;

import java.util.List;

import com.code.aon.account.bridge.CustomerAccount;
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

public class CustomerAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		CustomerAccount customerAccount = (CustomerAccount) evt.getTo();
		checkIfCustomerAssigned(customerAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		CustomerAccount customerAccount = (CustomerAccount) evt.getTo();
		checkIfCustomerAssigned(customerAccount);
	}

	private void checkIfCustomerAssigned(CustomerAccount customerAccount) throws ManagerBeanVetoListenerException{
		try {
			if (customerAccount.getCustomer() == null) {
				throw new ManagerBeanVetoListenerException("El cliente es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(CustomerAccount.class);
			Integer id = customerAccount.getCustomer().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( customerAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.CUSTOMER_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, customerAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				CustomerAccount dup = (CustomerAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El cliente ");
				sb.append(customerAccount.getAccountDescription());
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
