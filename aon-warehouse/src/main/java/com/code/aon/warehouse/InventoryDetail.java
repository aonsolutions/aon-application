package com.code.aon.warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.InventoryDetailDB;

@Entity
@Table(name="inventory_detail")
public class InventoryDetail extends InventoryDetailDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public double getCostAmount() {
		return CommonUtil.round(getCost() * getRealQuantity());
	}
	
}