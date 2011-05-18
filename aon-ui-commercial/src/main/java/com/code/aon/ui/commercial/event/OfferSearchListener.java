package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class OfferSearchListener extends ControllerSearchListener {

	private OfferType offerType;

	private Target target;

	private Supplier supplier;

	private Seller seller;
	
	private OfferStatus[] offerStatuses;

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

	@Override
	protected void init() throws ManagerBeanException {
		setOfferType(null);
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		setSeller( (Seller) sellerBean.createNewTo() );
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		setTarget( (Target) targetBean.createNewTo() );
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		setSupplier( (Supplier) supplierBean.createNewTo() );
		OfferStatus[] defaultOfferStatus = {OfferStatus.PENDING};
		setOfferStatuses(defaultOfferStatus);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getOfferType() != null) {
			criteria.addEqualExpression(getFieldName(ICommercialAlias.OFFER_TYPE), getOfferType());			
		}
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getFieldName(ICommercialAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(ICommercialAlias.OFFER_SUPPLIER_ID), getSupplier().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(ICommercialAlias.OFFER_SELLER_ID), getSeller().getId());			
		}
		if (!ArrayUtils.isEmpty(getOfferStatuses())) {
			String status = getController().resolveAlias(ICommercialAlias.OFFER_STATUS);
			addEnumToCriteria(criteria, status, getOfferStatuses());
		}
	}	

}