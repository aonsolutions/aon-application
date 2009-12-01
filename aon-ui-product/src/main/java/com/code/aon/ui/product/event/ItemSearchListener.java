package com.code.aon.ui.product.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class ItemSearchListener extends RegistrySearchListener {

	private ProductStatus[] itemStatuses;
	
	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}

	public void setItemStatuses(ProductStatus[] itemStatuses) {
		this.itemStatuses = itemStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		ProductStatus[] defaultItemStatus = {ProductStatus.ACTIVE};
		setItemStatuses(defaultItemStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (!ArrayUtils.isEmpty(getItemStatuses())) {
			String status = getController().resolveAlias(IProductAlias.ITEM_STATUS);
			addEnumToCriteria(criteria, status, getItemStatuses());
		}
		super.completeCriteria();
	}
	
}