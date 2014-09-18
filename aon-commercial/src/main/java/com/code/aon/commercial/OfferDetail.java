package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.strategy.ICalculable;
import com.esferalia.aon.entity.master.OfferDetailDB;

@Entity
@Table(name="offer_detail")
public class OfferDetail extends OfferDetailDB implements ICalculable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setPrice(double price) {
		super.setPrice( CommonUtil.round(price, 4) );
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