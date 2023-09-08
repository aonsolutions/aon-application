package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.strategy.ICalculable;
import com.esferalia.aon.entity.master.OfferDetailDB;

@Entity
@Table(name="offer_detail")
public class OfferDetail extends OfferDetailDB implements ICalculable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setPrice(double price) {
		super.setPrice( CommonUtil.round(price, 4) );
	}

	@Transient
	public boolean isPending() {
		return getStatus() == OfferDetailStatus.PENDING;
	}

	@Transient
	public boolean isOnSale() {
		return getStatus() == OfferDetailStatus.ON_SALE;
	}

	@Transient
	public boolean isOnInvoice() {
		return getStatus() == OfferDetailStatus.ON_INVOICE;
	}

	@Transient
	public double getTaxes() {
		return 0;
	}

	@Transient
	public WorkPlace getWorkPlace() {
		return getOffer().getWorkPlace();
	}

}