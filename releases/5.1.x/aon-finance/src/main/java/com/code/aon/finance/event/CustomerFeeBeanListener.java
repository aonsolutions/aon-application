package com.code.aon.finance.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class CustomerFeeBeanListener extends ManagerBeanListenerAdapter {
	
	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		CustomerFee fee = (CustomerFee)evt.getTo();
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), fee.getCustomer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ID), fee.getId()));
		criteria.addGreaterThanOrEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE), fee.getLine());
		criteria.addOrder(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE));
		List<ITransferObject> list = feeBean.getList(criteria);
		int index = fee.getLine();
		for (ITransferObject to : list) {
			CustomerFee customerFee = (CustomerFee)to;
			if (index == customerFee.getLine()) {
				customerFee.setLine(index + 1);
				feeBean.update(customerFee);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		CustomerFee fee = (CustomerFee)evt.getTo();
		if (!updating) {
			updating = true;

			IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), fee.getCustomer().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ID), fee.getId()));
			criteria.addEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE), fee.getLine());
			if (feeBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), fee.getCustomer().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ID), fee.getId()));
				criteria.addOrder(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE));
				List<ITransferObject> list = feeBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					CustomerFee customerFee = (CustomerFee)to;
					if (index == fee.getLine()) {
						++index;
					}
					customerFee.setLine(index);
					feeBean.update(customerFee);
					++index;
				}
			}

			updating = false;
		}
	}
	
	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		CustomerFee fee = (CustomerFee)evt.getTo();
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), fee.getCustomer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ID), fee.getId()));
		criteria.addGreaterThanOrEqualExpression(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE), fee.getLine());
		criteria.addOrder(feeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_LINE));
		List<ITransferObject> list = feeBean.getList(criteria);
		int index = fee.getLine() + 1;
		for (ITransferObject to : list) {
			CustomerFee customerFee = (CustomerFee)to;
			if (index == customerFee.getLine()) {
				customerFee.setLine(index - 1);
				feeBean.update(customerFee);
				++ index;
			}
		}
	}

}
