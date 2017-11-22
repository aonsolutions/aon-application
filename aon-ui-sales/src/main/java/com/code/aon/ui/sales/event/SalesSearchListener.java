package com.code.aon.ui.sales.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesSearchListener extends RegistrySearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Sales_customer_registry_";

	private Customer customer;
	private Seller seller;
	private Project project;
	private SalesStatus[] salesStatuses;
    private Item item;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}	
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Seller getSeller() {
		return seller;
	}
	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	public SalesStatus[] getSalesStatuses() {
		return salesStatuses;
	}
	public void setSalesStatuses(SalesStatus[] salesStatuses) {
		this.salesStatuses = salesStatuses;
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
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		SalesStatus[] defaultSalesStatus = {SalesStatus.PENDING};
		setSalesStatuses(defaultSalesStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.SALES_CUSTOMER_ID), getCustomer().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.SALES_SELLER_ID), getSeller().getId());			
		}
		if (getProject() != null && getProject().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.SALES_PROJECT_ID), getProject().getId());			
		}
		if (!ArrayUtils.isEmpty(getSalesStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.SALES_STATUS);
			addEnumToCriteria(criteria, status, getSalesStatuses());
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression("Sales.lines.item.id", getItem().getId());
		}				
	}	
}