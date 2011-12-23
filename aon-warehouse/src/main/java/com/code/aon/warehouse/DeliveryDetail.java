package com.code.aon.warehouse;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.strategy.ICalculable;
import com.esferalia.aon.entity.master.DeliveryDetailDB;

@Entity
@Table(name="delivery_detail")
public class DeliveryDetail extends DeliveryDetailDB implements ICalculable, IStockable {
	
	private static final long serialVersionUID = 1L;

	public void setPrice(double price) {
        setPrice(CommonUtil.round(price, 4));
	}

	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
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