package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IFinderBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;

public class CustomerAccountChecker extends RegistryAccountChecker  {

	private static final long serialVersionUID = 3829325207456286992L;
	
	private IManagerBean customerAccountBean;
	private IManagerBean customerBean;

	@Override
	protected String getCompanyNameAlias() throws ManagerBeanException {
		if (customerBean == null) {
			customerBean = BeanManager.getManagerBean(Customer.class);
		}
		String alias = customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_NAME);
		return alias;
	}
	@Override
	protected String getRegistryAccountIDAlias() throws ManagerBeanException {
		String alias= getIAccountBean().getFieldName(IAccountBridgeAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID);
		return alias;
	}

	@Override
	protected String getBackAction() {
		return "customerAccountChecker_list";
	}

	@Override
	protected IFinderBean getIAccountBean() throws ManagerBeanException {
		if (customerAccountBean == null) {
			customerAccountBean = BeanManager.getManagerBean(CustomerAccount.class);
		}
		return customerAccountBean;
	}
	@Override
	protected String getPojoName() {
		return "Customer";
	}

}
