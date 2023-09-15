package com.code.aon.warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.strategy.ICalculable;
import com.esferalia.aon.entity.master.DeliveryDetailDB;

@Entity
@Table(name="delivery_detail")
public class DeliveryDetail extends DeliveryDetailDB implements ICalculable, IStockable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
	}

	@Transient
	public double getTaxes() {
		return 0;
	}

	@Transient
	public WorkPlace getWorkPlace() {
		return getDelivery() != null ? getDelivery().getWorkPlace() : null;
	}

	@Transient
	public boolean isEntry() {
		return false;
	}

	@Transient
	public String getTableName() {
		return "delivery_detail";
	}

}