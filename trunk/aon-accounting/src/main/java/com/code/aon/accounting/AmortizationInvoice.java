package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AmortizationInvoiceDB;

@Entity
@Table(name="amortization_invoice")
public class AmortizationInvoice extends AmortizationInvoiceDB {

	private static final long serialVersionUID = -6476880317614675916L;
	
}
