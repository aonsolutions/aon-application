package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.esferalia.aon.entity.master.PrepaymentDB;

@Entity
@Table(name="prepayment")
public class Prepayment extends PrepaymentDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isCollectFee() {
		return getCollect() == PrepaymentCollect.FEE;
	}

	@Transient
	public boolean isCollectInvoice() {
		return getCollect() == PrepaymentCollect.INVOICE_DETAIL;
	}

}