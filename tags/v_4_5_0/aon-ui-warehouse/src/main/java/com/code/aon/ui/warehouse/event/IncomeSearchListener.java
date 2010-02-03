package com.code.aon.ui.warehouse.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeSearchListener extends ControllerSearchListener {

	private Supplier supplier;

	private IncomeStatus[] incomeStatuses;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public IncomeStatus[] getIncomeStatuses() {
		return incomeStatuses;
	}

	public void setIncomeStatuses(IncomeStatus[] incomeStatuses) {
		this.incomeStatuses = incomeStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setSupplier(new Supplier());
		IncomeStatus[] defaultIncomeStatus = {IncomeStatus.PENDING};
		setIncomeStatuses(defaultIncomeStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getIncomeStatuses())) {
			String status = getController().resolveAlias(IWarehouseAlias.INCOME_STATUS);
			addEnumToCriteria(criteria, status, getIncomeStatuses());
		}
	}	
}