package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IFinderBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;

public class SupplierAccountChecker extends RegistryAccountChecker  {

	private static final long serialVersionUID = 3829325207456286992L;
	
	private IManagerBean supplierAccountBean;
	private IManagerBean supplierBean;

	@Override
	protected String getCompanyNameAlias() throws ManagerBeanException {
		if (supplierBean == null) {
			supplierBean = BeanManager.getManagerBean(Supplier.class);
		}
		String alias = supplierBean.getFieldName(ISupplierAlias.SUPPLIER_REGISTRY_NAME);
		return alias;
	}
	@Override
	protected String getRegistryAccountIDAlias() throws ManagerBeanException {
		String alias= getIAccountBean().getFieldName(IAccountBridgeAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID);
		return alias;
	}

	@Override
	protected String getBackAction() {
		return "supplierAccountChecker_list";
	}

	@Override
	protected IFinderBean getIAccountBean() throws ManagerBeanException {
		if (supplierAccountBean == null) {
			supplierAccountBean = BeanManager.getManagerBean(SupplierAccount.class);
		}
		return supplierAccountBean;
	}
	@Override
	protected String getPojoName() {
		return "Supplier";
	}

}
