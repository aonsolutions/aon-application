package com.code.aon.sales;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.esferalia.aon.entity.master.SalesDetailDB;

@Entity
@Table(name="sales_detail")
public class SalesDetail extends SalesDetailDB implements ICalculable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private double transfered;

    public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }

    @Transient
	public double getPendingQuantity() {
		return CommonUtil.round(getQuantity() - getDelivered(), 3);
	}
	@Transient
	public double getTransfered() {
		double pending = getPendingQuantity();
		transfered = transfered > pending ? pending : transfered;
		return transfered;
	}
	public void setTransfered(double transfered) {
		this.transfered = transfered;
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

}