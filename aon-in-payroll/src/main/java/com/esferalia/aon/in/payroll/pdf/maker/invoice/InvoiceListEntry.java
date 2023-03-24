package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public class InvoiceListEntry implements Serializable {
	private Date date;
	private String number;
	private String nif;
	private String name;
	private Double base;
	private Double iva;
	private Double irpf;
	private Double total;
	
	public InvoiceListEntry() {
	}
	public InvoiceListEntry(Invoice invoice) {
		if (invoice != null) {
			date = invoice.getIssueDate();
			number = invoice.getReferenceCode();
			nif = invoice.getRegistryDocument();
			name = invoice.getRegistryName();
			base = invoice.getTaxableBase();
			iva = invoice.getVatQuota();
			irpf = invoice.getRetentionQuota();
			total = invoice.getTotal();
		}
	}
	
	public Date getDate() {
		return date;
	}
	public InvoiceListEntry setDate(Date date) {
		this.date = date;
		return this;
	}
	public String getNumber() {
		return number;
	}
	public InvoiceListEntry setNumber(String number) {
		this.number = number;
		return this;
	}
	public String getNif() {
		return nif;
	}
	public InvoiceListEntry setNif(String nif) {
		this.nif = nif;
		return this;
	}
	public String getName() {
		return name;
	}
	public InvoiceListEntry setName(String name) {
		this.name = name;
		return this;
	}
	public Double getBase() {
		return base;
	}
	public InvoiceListEntry setBase(Double base) {
		this.base = base;
		return this;
	}
	public Double getIva() {
		return iva;
	}
	public InvoiceListEntry setIva(Double iva) {
		this.iva = iva;
		return this;
	}
	public Double getIrpf() {
		return irpf;
	}
	public InvoiceListEntry setIrpf(Double irpf) {
		this.irpf = irpf;
		return this;
	}
	public Double getTotal() {
		return total;
	}
	public InvoiceListEntry setTotal(Double total) {
		this.total = total;
		return this;
	}
	
	
}