package com.code.aon.account.bridge.event;


import java.util.List;

import com.code.aon.account.bridge.TaxAccount;
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

public class TaxAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		TaxAccount taxAccount = (TaxAccount) evt.getTo();
		checkIfTaxAssigned(taxAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		TaxAccount taxAccount = (TaxAccount) evt.getTo();
		checkIfTaxAssigned(taxAccount);
	}

	private void checkIfTaxAssigned(TaxAccount taxAccount) throws ManagerBeanVetoListenerException{
		try {
			if (taxAccount.getTax() == null) {
				throw new ManagerBeanVetoListenerException("El impuesto es un dato requerido.");	
			}
			if (taxAccount.getType() == null) {
				throw new ManagerBeanVetoListenerException("El tipo de impuesto es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(TaxAccount.class);
			Integer id = taxAccount.getTax().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID);
			String typeAlias = bean.getFieldName( IAccountBridgeAlias.TAX_ACCOUNT_TYPE);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			c.addEqualExpression(typeAlias, taxAccount.getType());
			if ( taxAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.TAX_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, taxAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				TaxAccount dup = (TaxAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El impuesto ");
				sb.append(taxAccount.getAccountDescription());
				sb.append(" de este tipo ya está enlazado con la cuenta ");
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
