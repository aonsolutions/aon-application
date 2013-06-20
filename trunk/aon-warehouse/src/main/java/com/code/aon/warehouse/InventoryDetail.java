package com.code.aon.warehouse;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.InventoryDetailDB;

@Entity
@Table(name="inventory_detail")
public class InventoryDetail extends InventoryDetailDB {
	
	private static final long serialVersionUID = -8584837639559704341L;

	@Transient
	public double getCostAmount() {
		return CommonUtil.round(getCost() * getRealQuantity());
	}
	
}