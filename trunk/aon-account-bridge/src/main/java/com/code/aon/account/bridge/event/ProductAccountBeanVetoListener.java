package com.code.aon.account.bridge.event;



import java.util.List;

import com.code.aon.account.bridge.ProductAccount;
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

public class ProductAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ProductAccount productAccount = (ProductAccount) evt.getTo();
		checkIfProductAssigned(productAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ProductAccount productAccount = (ProductAccount) evt.getTo();
		checkIfProductAssigned(productAccount);
	}

	private void checkIfProductAssigned(ProductAccount productAccount) throws ManagerBeanVetoListenerException{
		try {
			if (productAccount.getProduct() == null) {
				throw new ManagerBeanVetoListenerException("El artículo es un dato requerido.");	
			}
			if (productAccount.getType() == null) {
				throw new ManagerBeanVetoListenerException("El tipo de artículo es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(ProductAccount.class);
			Integer id = productAccount.getProduct().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID);
			String typeAlias = bean.getFieldName( IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			c.addEqualExpression(typeAlias, productAccount.getType());
			if ( productAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.PRODUCT_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, productAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				ProductAccount dup = (ProductAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El artículo ");
				sb.append(productAccount.getAccountDescription());
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
