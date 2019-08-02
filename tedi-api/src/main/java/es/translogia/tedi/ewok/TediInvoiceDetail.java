package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediInvoiceDetail implements Serializable{

	private static final long serialVersionUID = -4491778828369820071L;
	
	private String description;
	private Double quantity;
	private Double price;
	private Double discount;
	private Double base;
	private Double vat;
	private Double surcharge;

	public String getDescription() {
		return description;
	}

	public TediInvoiceDetail setDescription(String description) {
		this.description = description;
		return this;
	}

	public Double getQuantity() {
		return quantity;
	}

	public TediInvoiceDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public TediInvoiceDetail setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getDiscount() {
		return discount;
	}

	public TediInvoiceDetail setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public TediInvoiceDetail setBase(Double base) {
		this.base = base;
		return this;
	}

	public Double getVat() {
		return vat;
	}

	public TediInvoiceDetail setVat(Double vat) {
		this.vat = vat;
		return this;
	}

	public Double getSurcharge() {
		return surcharge;
	}

	public TediInvoiceDetail setSurcharge(Double surcharge) {
		this.surcharge = surcharge;
		return this;
	}
}
