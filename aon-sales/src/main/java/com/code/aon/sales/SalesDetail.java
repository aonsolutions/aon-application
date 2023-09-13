package com.code.aon.sales;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.esferalia.aon.entity.master.SalesDetailDB;

@Entity
@Table(name="sales_detail")
public class SalesDetail extends SalesDetailDB implements ICalculable, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private double transfered;
    private boolean forcePendingQuantityCancel;
	private boolean skipOfferUpdate;

	public SalesDetail() {
		setSkipOfferUpdate(false);
	}

	public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }

    @Transient
	public double getPendingQuantity() {
		return CommonUtil.round(getQuantity() - getDelivered(), 3);
	}

    @Transient
	public double getTransfered() {
		return transfered;
	}
	public void setTransfered(double transfered) {
		this.transfered = transfered;
	}

	@Transient
	public boolean isForcePendingQuantityCancel() {
		return forcePendingQuantityCancel;
	}
	public void setForcePendingQuantityCancel(boolean forcePendingQuantityCancel) {
		this.forcePendingQuantityCancel = forcePendingQuantityCancel;
	}

	@Transient
	public boolean isSkipOfferUpdate() {
		return skipOfferUpdate;
	}
	public void setSkipOfferUpdate(boolean skipOfferUpdate) {
		this.skipOfferUpdate = skipOfferUpdate;
	}

	@Transient
	public boolean isPending() {
		return getStatus() == SalesDetailStatus.PENDING;
	}
	@Transient
	public boolean isPartialSettled() {
		return getStatus() == SalesDetailStatus.PARTIAL_SETTLED;
	}
	@Transient
	public boolean isSettled() {
		return getStatus() == SalesDetailStatus.SETTLED;
	}

	@Transient
	public WorkPlace getWorkPlace() {
		return getSales().getWorkPlace();
	}

}