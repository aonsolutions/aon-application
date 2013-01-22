package com.code.aon.ui.accounting.controller.amortization;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class AmortizationTypeController extends BasicController {

	private boolean yearsEnabled;

	public boolean isYearsEnabled() {
		return yearsEnabled;
	}

	public void setYearsEnabled(boolean yearsEnabled) {
		this.yearsEnabled= yearsEnabled;
	}
	
	private List<SelectItem> getAccounts(Criteria criteria) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		List<ITransferObject> accounts = accountBean.getList(criteria); 
		for (ITransferObject to : accounts ) {
			Account account = (Account) to;
			SelectItem item = new SelectItem(account.getCode(), account.getFullDescription());
			list.add(item);
		}
		return list;
	}
	
	public List<SelectItem> getFixedAssetAccounts() throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "20*|21*|22*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria);
	}
	public List<SelectItem> getAccumulatedDepreciationAccounts() throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "280*|281*|282*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria);
	}
	public List<SelectItem> getAmortizationAllocationAccounts() throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "680*|681*|682*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria);
	}
	
}
