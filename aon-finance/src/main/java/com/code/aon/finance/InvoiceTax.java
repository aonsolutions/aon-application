package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.enumeration.TaxType;
import com.esferalia.aon.entity.master.InvoiceTaxDB;

@Entity
@Table(name="invoice_tax")
public class InvoiceTax extends InvoiceTaxDB implements ITransferObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isVat() {
		return getTaxType() == TaxType.VAT;
	}

	@Transient
	public boolean isRetention() {
		return getTaxType() == TaxType.RETENTION;
	}

}