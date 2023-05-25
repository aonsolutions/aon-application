package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.OfferDetailCommissionDB;

@Entity
@Table(name="offer_detail_commission")
public class OfferDetailCommission extends OfferDetailCommissionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public Offer getOffer() {
		return (getOfferDetail()!=null?getOfferDetail().getOffer():null);
	}
	
}
