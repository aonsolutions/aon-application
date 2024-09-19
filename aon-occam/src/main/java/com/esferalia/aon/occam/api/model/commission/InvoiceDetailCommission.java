package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.InvoiceFlat;

public class InvoiceDetailCommission implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private InvoiceFlat invoiceFlat;
	private Double commission;
	private Double amount;
	private InvoiceDetailCommissionStatus status;
	private Date payDate;
	
	public Integer getId() {
		return id;
	}
	public InvoiceDetailCommission setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public InvoiceDetailCommission setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public InvoiceFlat getInvoiceFlat() {
		return invoiceFlat;
	}
	public InvoiceDetailCommission setInvoiceFlat(InvoiceFlat invoiceDetail) {
		this.invoiceFlat = invoiceDetail;
		return this;
	}
	public Double getCommission() {
		return commission;
	}
	public InvoiceDetailCommission setCommission(Double commission) {
		this.commission = commission;
		return this;
	}
	public Double getAmount() {
		return amount;
	}
	public InvoiceDetailCommission setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	public InvoiceDetailCommissionStatus getStatus() {
		return status;
	}
	public InvoiceDetailCommission setStatus(InvoiceDetailCommissionStatus status) {
		this.status = status;
		return this;
	}
	public Date getPayDate() {
		return payDate;
	}
	public InvoiceDetailCommission setPayDate(Date payDate) {
		this.payDate = payDate;
		return this;
	}
	
}
