package com.code.aon.product;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.CatalogueItemDB;

@Entity
@Table(name="catalogue_item")
@Heritable
public class CatalogueItem extends CatalogueItemDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setQuantity(double quantity) {
        super.setQuantity( CommonUtil.round(quantity, 3));
	}

	public void setPrice(double price) {
        super.setPrice( CommonUtil.round(price, 4));
	}

	public void setDiscount(double discount) {
		super.setDiscount( CommonUtil.round(discount));
	}
	
	@Transient
	public String getItemName() {
		if (getItem() != null && getItem().getId() != null) {
			return getItem().getFullName();
		} else {
			return getProduct().getName();
		}
	}

}
