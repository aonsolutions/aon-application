package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.OfferTermDB;

@Entity
@Table(name="offer_term")
public class OfferTerm extends OfferTermDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}