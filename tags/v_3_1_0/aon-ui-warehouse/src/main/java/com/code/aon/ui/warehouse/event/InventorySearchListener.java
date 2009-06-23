package com.code.aon.ui.warehouse.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class InventorySearchListener extends ControllerSearchListener {

	private Date inventoryDateFrom;
	
	private Date inventoryDateTo;
	
	public Date getInventoryDateFrom() {
		return inventoryDateFrom;
	}

	public void setInventoryDateFrom(Date inventoryDateFrom) {
		this.inventoryDateFrom = inventoryDateFrom;
	}

	public Date getInventoryDateTo() {
		return inventoryDateTo;
	}

	public void setInventoryDateTo(Date inventoryDateTo) {
		this.inventoryDateTo = inventoryDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setInventoryDateFrom(null);
		setInventoryDateTo(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getInventoryDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IWarehouseAlias.INVENTORY_INVENTORY_DATE), getInventoryDateFrom());
		}
		if (getInventoryDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IWarehouseAlias.INVENTORY_INVENTORY_DATE), getInventoryDateTo());
		}
	}

}