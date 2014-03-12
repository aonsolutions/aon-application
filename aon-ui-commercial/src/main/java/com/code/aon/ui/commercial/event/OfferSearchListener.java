package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private OfferType offerType;

	private Target target;

	private Supplier supplier;

	private Seller seller;
	
	private OfferStatus[] offerStatuses;
	
	private Project project;

	public OfferType getOfferType() {
		return offerType;
	}

	public void setOfferType(OfferType offerType) {
		this.offerType = offerType;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
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
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setOfferType(null);
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setTarget((Target)BeanManager.getManagerBean(Target.class).createNewTo());
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		OfferStatus[] defaultOfferStatus = {OfferStatus.PENDING};
		setOfferStatuses(defaultOfferStatus);
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getOfferType() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_TYPE), getOfferType());			
		}
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_SUPPLIER_ID), getSupplier().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_SELLER_ID), getSeller().getId());			
		}
		if (!ArrayUtils.isEmpty(getOfferStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.OFFER_STATUS);
			addEnumToCriteria(criteria, status, getOfferStatuses());
		}
		if ( getProject()!=null && getProject().getId()!=null ) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.OFFER_PROJECT_ID), getProject().getId());			
		}
	}	

}