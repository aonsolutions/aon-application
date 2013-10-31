package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.esferalia.aon.entity.master.PrepaymentDB;

@Entity
@Table(name="prepayment")
public class Prepayment extends PrepaymentDB {

	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isCollectFee() {
		return getCollect() == PrepaymentCollect.FEE;
	}

	@Transient
	public boolean isCollectInvoice() {
		return getCollect() == PrepaymentCollect.INVOICE_DETAIL;
	}

}