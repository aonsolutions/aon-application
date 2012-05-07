package com.code.aon.account.bridge.event;



import java.util.List;

import com.code.aon.account.bridge.PayMethodTypeDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class PayMethodTypeDetailAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PayMethodTypeDetailAccount payMethodTypeDetailAccount = (PayMethodTypeDetailAccount) evt.getTo();
		checkIfPayMethodTypeDetailAssigned(payMethodTypeDetailAccount);
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
			PayMethodTypeDetail pmtda =  payMethodTypeDetailAccount.getPayMethodTypeDetail();
			pmtda =  (PayMethodTypeDetail) bean.insert( pmtda );
			payMethodTypeDetailAccount.setPayMethodTypeDetail( pmtda );
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PayMethodTypeDetailAccount payMethodTypeDetailAccount = (PayMethodTypeDetailAccount) evt.getTo();
		checkIfPayMethodTypeDetailAssigned(payMethodTypeDetailAccount);
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
			PayMethodTypeDetail pmtda =  (PayMethodTypeDetail) bean.update( payMethodTypeDetailAccount.getPayMethodTypeDetail() );
			payMethodTypeDetailAccount.setPayMethodTypeDetail( pmtda );
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	private void checkIfPayMethodTypeDetailAssigned(PayMethodTypeDetailAccount payMethodTypeDetailAccount) throws ManagerBeanVetoListenerException{
		try {
			if (payMethodTypeDetailAccount.getPayMethodTypeDetail() == null) {
				throw new ManagerBeanVetoListenerException("La forma de pago es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(PayMethodTypeDetailAccount.class);
			Integer id = payMethodTypeDetailAccount.getPayMethodTypeDetail().getId();
			String alias = bean.getFieldName( IEntityAlias.PAY_METHOD_TYPE_DETAIL_ACCOUNT_PAY_METHOD_TYPE_DETAIL_ID );
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( payMethodTypeDetailAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IEntityAlias.PAY_METHOD_TYPE_DETAIL_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, payMethodTypeDetailAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				ProductAccount dup = (ProductAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El tipo de pago ");
				sb.append(payMethodTypeDetailAccount.getPayMethodTypeDetail().getDescription());
				sb.append(" ya está enlazado con la cuenta ");
				sb.append(dup.getAccount().getFullDescription());
				sb.append(".");
				throw new ManagerBeanVetoListenerException(sb.toString()); 
			}
			
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e); 
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		try {
			PayMethodTypeDetailAccount pmtda = (PayMethodTypeDetailAccount) evt.getTo();
			IManagerBean ftBean = BeanManager.getManagerBean(FinanceTracking.class);
			Criteria c = new Criteria();
			c.addEqualExpression(ftBean.getFieldName(IEntityAlias.FINANCE_TRACKING_PAY_METHOD_TYPE_DETAIL_ID), pmtda.getPayMethodTypeDetail().getId() );
			int size = ftBean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Existen movimientos de vencimientos que apuntan a esta cuenta");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e); 
		}
		
	}
}
