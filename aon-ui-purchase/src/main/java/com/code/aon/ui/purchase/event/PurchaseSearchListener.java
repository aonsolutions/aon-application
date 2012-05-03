package com.code.aon.ui.purchase.event;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.Item;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseSearchListener extends RegistrySearchListener {
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Purchase_supplier_registry_";

	private Supplier supplier;
	
	private WorkPlace workPlace;

	private PurchaseStatus[] purchaseStatuses;
	
	private Item item;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}		
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public PurchaseStatus[] getPurchaseStatuses() {
		return purchaseStatuses;
	}

	public void setPurchaseStatuses(PurchaseStatus[] purchaseStatuses) {
		this.purchaseStatuses = purchaseStatuses;
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
		PurchaseStatus[] defaultPurchaseStatus = {PurchaseStatus.PENDING};
		setPurchaseStatuses(defaultPurchaseStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );		
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PURCHASE_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			criteria.addInExpression(getFieldName(IEntityAlias.PURCHASE_WORK_PLACE_ID), getCurrentUserWorkPlacesIds());
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PURCHASE_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getPurchaseStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PURCHASE_STATUS);
			addEnumToCriteria(criteria, status, getPurchaseStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Purchase.lines.item.id", getItem().getId());
		}				
	}
	
	private List<Integer> getCurrentUserWorkPlacesIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for(ITransferObject to: getCurrentUserWorkPlaces()){
			WorkPlace wp = (WorkPlace) to;
			list.add(wp.getId());
		}
		return list;
	}
	
	private List<ITransferObject> getCurrentUserWorkPlaces() throws ManagerBeanException {
   		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
   		Criteria criteria = new Criteria();
   		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
   		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
    	criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
    	return workPlaceBean.getList(criteria);
	}	

}