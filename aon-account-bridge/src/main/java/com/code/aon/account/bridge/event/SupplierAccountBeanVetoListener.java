package com.code.aon.account.bridge.event;


import java.util.List;

import com.code.aon.account.bridge.SupplierAccount;
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

public class SupplierAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		SupplierAccount supplierAccount = (SupplierAccount) evt.getTo();
		checkIfSupplierAssigned(supplierAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		SupplierAccount supplierAccount = (SupplierAccount) evt.getTo();
		checkIfSupplierAssigned(supplierAccount);
	}

	private void checkIfSupplierAssigned(SupplierAccount supplierAccount) throws ManagerBeanVetoListenerException{
		try {
			if (supplierAccount.getSupplier() == null) {
				throw new ManagerBeanVetoListenerException("El proveedor es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(SupplierAccount.class);
			Integer id = supplierAccount.getSupplier().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( supplierAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.SUPPLIER_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, supplierAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				SupplierAccount dup = (SupplierAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El proveedor ");
				sb.append(supplierAccount.getAccountDescription());
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
