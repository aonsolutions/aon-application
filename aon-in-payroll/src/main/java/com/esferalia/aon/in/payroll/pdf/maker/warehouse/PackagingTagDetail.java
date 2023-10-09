package com.esferalia.aon.in.payroll.pdf.maker.warehouse;

import com.esferalia.aon.occam.api.model.product.Item;

public class PackagingTagDetail {
	
	private Item item;	
	private String barcode;
	private Double quantity;
	private String sscc;
	private String ean128;
	
	public PackagingTagDetail() {
	
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getSscc() {
		return sscc;
	}

	public void setSscc(String sscc) {
		this.sscc = sscc;
	}

	public String getEan128() {
		return ean128;
	}

	public void setEan128(String ean128) {
		this.ean128 = ean128;
	}
}
