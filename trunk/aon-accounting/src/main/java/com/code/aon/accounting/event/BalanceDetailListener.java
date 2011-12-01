package com.code.aon.accounting.event;

import java.util.List;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * @author Consulting & Development
 * 
 */
public class BalanceDetailListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		BalanceDetail bd = (BalanceDetail) evt.getTo();
		
		IManagerBean bdBean = BeanManager.getManagerBean(BalanceDetail.class);
		Criteria criteria = new Criteria();
		String balanceAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID);
		String sortKeyAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY);
		String detailIdAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_ID);
		criteria.addEqualExpression(balanceAlias, bd.getBalance().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailIdAlias, bd.getId()));
		criteria.addGreaterThanOrEqualExpression(sortKeyAlias, bd.getSortKey());
		criteria.addOrder(sortKeyAlias);
		List<ITransferObject> list = bdBean.getList(criteria);
		int index = bd.getSortKey(); 
		for (ITransferObject to : list ){
			BalanceDetail b = (BalanceDetail) to;
			index++;
			b.setSortKey(index);
			bdBean.update(b);
		}
	}
	
	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		BalanceDetail bd = (BalanceDetail) evt.getTo();
		
		IManagerBean bdBean = BeanManager.getManagerBean(BalanceDetail.class);
		Criteria criteria = new Criteria();
		String balanceAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID);
		String sortKeyAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY);
		String detailIdAlias = bdBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_ID);
		criteria.addEqualExpression(balanceAlias, bd.getBalance().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailIdAlias, bd.getId()));
		criteria.addGreaterThanOrEqualExpression(sortKeyAlias, bd.getSortKey());
		criteria.addOrder(sortKeyAlias);
		List<ITransferObject> list = bdBean.getList(criteria);
		for (ITransferObject to : list ){
			BalanceDetail b = (BalanceDetail) to;
			b.setSortKey(b.getSortKey()-1);
			bdBean.update(b);
		}
	}
}
