package com.code.aon.ui.warehouse.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeSearchListener extends RegistrySearchListener {
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Income_supplier_registry_";

	private Supplier supplier;

	private IncomeStatus[] incomeStatuses;
	
    private Item item;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}		

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
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		IncomeStatus[] defaultIncomeStatus = {IncomeStatus.PENDING};
		setIncomeStatuses(defaultIncomeStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria);
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INCOME_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getIncomeStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.INCOME_STATUS);
			addEnumToCriteria(criteria, status, getIncomeStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Income.lines.item.id", getItem().getId());
		}						
	}	
}