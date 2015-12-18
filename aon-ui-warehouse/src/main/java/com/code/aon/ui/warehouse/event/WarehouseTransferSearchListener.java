package com.code.aon.ui.warehouse.event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseTransferSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    private Warehouse sourceWarehouse;
    private Warehouse targetWarehouse;
    private Item item;
    
	public Warehouse getSourceWarehouse() {
		return sourceWarehouse;
	}
	public void setSourceWarehouse(Warehouse sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
	}

	public Warehouse getTargetWarehouse() {
		return targetWarehouse;
	}
	public void setTargetWarehouse(Warehouse targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
	}

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setSourceWarehouse((Warehouse)BeanManager.getManagerBean(Warehouse.class).createNewTo());
		setTargetWarehouse((Warehouse)BeanManager.getManagerBean(Warehouse.class).createNewTo());
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getSourceWarehouse()!=null && getSourceWarehouse().getId()!=null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE_ID), getSourceWarehouse().getId());
		} else {
			Expression expr1 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE));
			Expression expr2 = ExpressionUtilities.getInExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE_ID), getMyScopeWarehouses());
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
		if (getTargetWarehouse()!=null && getTargetWarehouse().getId()!=null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE_ID), getTargetWarehouse().getId());
		} else {
			Expression expr1 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE));
			Expression expr2 = ExpressionUtilities.getInExpression(getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE_ID), getMyScopeWarehouses());
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression("WarehouseTransfer.details.item.id", getItem().getId());
		}		
	}

	private Collection<Integer> getMyScopeWarehouses() throws ManagerBeanException {
		List<Integer> warehouseIds = new LinkedList<Integer>();
		for (Warehouse warehouse : WarehouseCollectionsController.getWarehouseList(null, false, true)) {
			warehouseIds.add(warehouse.getId());
		}
		if (warehouseIds.isEmpty()) {
			warehouseIds.add(-1);
		}
		return warehouseIds;
	}

}