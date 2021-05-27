package com.code.aon.ui.finance.event;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistryPayMethodSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CreditorAccountCheckerSearchListener extends RegistryPayMethodSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showEmptyAccountsOnly;
	private Account account;
	
	public boolean isShowEmptyAccountsOnly() {
		return showEmptyAccountsOnly;
	}

	public void setShowEmptyAccountsOnly(boolean showEmptyAccountsOnly) {
		this.showEmptyAccountsOnly = showEmptyAccountsOnly;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setShowEmptyAccountsOnly(false);
		setAccount((Account)BeanManager.getManagerBean(Account.class).createNewTo());
		super.init();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (isShowEmptyAccountsOnly()) {
			criteria.addNullExpression(getFieldName(IEntityAlias.CREDITOR_ACCOUNT));
		} else if (getAccount() != null && getAccount().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CREDITOR_ACCOUNT), getAccount().getId());
		}
		super.completeCriteria(criteria);
	}
	
}