package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferSearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String REGISTRY_SEARCH_PREFFIX = "Offer_target_registry_";

	private Target target;
	private Seller seller;
	private Project project;
	private Supplier supplier;
	private OfferType offerType;
	private OfferStatus[] offerStatuses;
    private Item item;

	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}	
	
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
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

	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public OfferType getOfferType() {
		return offerType;
	}
	public void setOfferType(OfferType offerType) {
		this.offerType = offerType;
	}

	public OfferStatus[] getOfferStatuses() {
		return offerStatuses;
	}
	public void setOfferStatuses(OfferStatus[] offerStatuses) {
		this.offerStatuses = offerStatuses;
	}

	public boolean isDealership() {
		return OfferType.DEALERSHIP == offerType;
	}
	
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setTarget((Target)BeanManager.getManagerBean(Target.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		setOfferType(null);
		OfferStatus[] defaultOfferStatus = {OfferStatus.PENDING};
		setOfferStatuses(defaultOfferStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_SELLER_ID), getSeller().getId());			
		}
		if (getProject()!=null && getProject().getId()!=null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_PROJECT_ID), getProject().getId());			
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_SUPPLIER_ID), getSupplier().getId());			
		}
		if (getOfferType() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_TYPE), getOfferType());			
		}
		if (!ArrayUtils.isEmpty(getOfferStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.OFFER_STATUS);
			addEnumToCriteria(criteria, status, getOfferStatuses());
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression("Offer.lines.item.id", getItem().getId());
		}				
	}	

}