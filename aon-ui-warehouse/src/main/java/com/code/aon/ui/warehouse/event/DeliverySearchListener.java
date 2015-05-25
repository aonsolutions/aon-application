package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliverySearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Delivery_customer_registry_";

	private Customer customer;

	private DeliveryStatus[] deliveryStatuses;

    private Item item;
    
    private Project project;
    
    private ProductCategory category;
    
    private Warehouse warehouse;
    
    private WorkPlace workPlace;
	
    
    
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}	
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DeliveryStatus[] getDeliveryStatuses() {
		return deliveryStatuses;
	}

	public void setDeliveryStatuses(DeliveryStatus[] deliveryStatuses) {
		this.deliveryStatuses = deliveryStatuses;
	}
	
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}			

	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		WarehouseCollectionsController wcc = (WarehouseCollectionsController) AonUtil.getRegisteredBean(IWarehouseConstants.COLLECTIONS_CONTROLLER_NAME);
		WorkPlace workPlace = getWorkPlace();
		List<SelectItem> warehouses = new LinkedList<SelectItem>();
		if(workPlace!=null){
			for (SelectItem selectItem :  wcc.getWarehouses()) {
				Warehouse w = (Warehouse) selectItem.getValue();	
				if(w.getWorkPlace().getId().equals(workPlace.getId())){
					warehouses.add(selectItem);
				}
			}
		}
		else{
			warehouses =  wcc.getWarehouses();
		}
		return warehouses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		DeliveryStatus[] defaultDeliveryStatus = {DeliveryStatus.PENDING};
		setDeliveryStatuses(defaultDeliveryStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		setCategory( (ProductCategory) BeanManager.getManagerBean(ProductCategory.class).createNewTo() );
		setWarehouse((Warehouse) BeanManager.getManagerBean(Warehouse.class).createNewTo());
		setWorkPlace((WorkPlace) BeanManager.getManagerBean(WorkPlace.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getDeliveryStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.DELIVERY_STATUS);
			addEnumToCriteria(criteria, status, getDeliveryStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Delivery.lines.item.id", getItem().getId());
		}
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DELIVERY_PROJECT_ID), getProject().getId());			
		}						
		if (getCategory() != null && getCategory().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Delivery_lines_item_product_category<id"), getCategory().getId());
		}		
		if (getWarehouse() !=null && getWarehouse().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Delivery_lines_warehouse_id"), getWarehouse().getId());
		}
	}	
}