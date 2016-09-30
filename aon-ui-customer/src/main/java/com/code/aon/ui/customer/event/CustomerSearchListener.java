package com.code.aon.ui.customer.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistryPayMethodSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerSearchListener extends RegistryPayMethodSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private CustomerStatus[] customerStatuses;
	private Item item;
	private boolean showComments;
	
	public CustomerStatus[] getCustomerStatuses() {
		return customerStatuses;
	}

	public void setCustomerStatuses(CustomerStatus[] customerStatuses) {
		this.customerStatuses = customerStatuses;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item registryItem) {
		this.item = registryItem;
	}	
	
	public boolean isShowComments() {
		return showComments;
	}

	public void setShowComments(boolean showComments) {
		this.showComments = showComments;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		CustomerStatus[] defaultCustomerStatus = {CustomerStatus.ACTIVE};
		setCustomerStatuses(defaultCustomerStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setShowComments(false);
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getCustomerStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.CUSTOMER_STATUS);
			addEnumToCriteria(criteria, status, getCustomerStatuses());
		}
		if (getItem() != null && getItem().getId() != null) {
			String item = getController().resolveAlias("Registry_items_item_id");
			criteria.addEqualExpression(item, getItem().getId());			
		}
		super.completeCriteria( criteria );
	}
	
}