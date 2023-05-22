package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AmortizationInvoiceDB;

@Entity
@Table(name="amortization_invoice")
public class AmortizationInvoice extends AmortizationInvoiceDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
