package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IFinderBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.dao.IFinanceAlias;

public class CreditorAccountChecker extends RegistryAccountChecker  {

	private static final long serialVersionUID = 3829325207456286992L;
	
	private IManagerBean creditorAccountBean;
	private IManagerBean creditorBean;

	@Override
	protected String getCompanyNameAlias() throws ManagerBeanException {
		if (creditorBean == null) {
			creditorBean = BeanManager.getManagerBean(Creditor.class);
		}
		String alias = creditorBean.getFieldName(IFinanceAlias.CREDITOR_REGISTRY_NAME);
		return alias;
	}
	@Override
	protected String getRegistryAccountIDAlias() throws ManagerBeanException {
		String alias= getIAccountBean().getFieldName(IAccountBridgeAlias.CREDITOR_ACCOUNT_CREDITOR_ID);
		return alias;
	}

	@Override
	protected String getBackAction() {
		return "creditorAccountChecker_list";
	}

	@Override
	protected IFinderBean getIAccountBean() throws ManagerBeanException {
		if (creditorAccountBean == null) {
			creditorAccountBean = BeanManager.getManagerBean(CreditorAccount.class);
		}
		return creditorAccountBean;
	}
	@Override
	protected String getPojoName() {
		return "Creditor";
	}

}
