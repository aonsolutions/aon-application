package com.code.aon.ui.supplier.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class SupplierSearchListener extends RegistrySearchListener {

	private SupplierStatus[] supplierStatuses;
	
	public SupplierStatus[] getSupplierStatuses() {
		return supplierStatuses;
	}

	public void setSupplierStatuses(SupplierStatus[] supplierStatuses) {
		this.supplierStatuses = supplierStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		SupplierStatus[] defaultSupplierStatus = {SupplierStatus.ACTIVE};
		setSupplierStatuses(defaultSupplierStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (!ArrayUtils.isEmpty(getSupplierStatuses())) {
			String status = getController().resolveAlias(ISupplierAlias.SUPPLIER_STATUS);
			addEnumToCriteria(criteria, status, getSupplierStatuses());
		}
		super.completeCriteria();
	}
	
}