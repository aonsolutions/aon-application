package es.translogia.tedi;

import org.json.JSONObject;

public class TediDetail {
	public TediDetail() {}
	
	public TediDetail(JSONObject json) {
		
	}
	
	private String description;
	private Double quantity;
	private Double price;
	private Double purchasePrice;
	private Double discount;
	
	public String getDescription() {
		return description;
	}
	public TediDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public TediDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public TediDetail setPrice(Double price) {
		this.price = price;
		return this;
	}
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public TediDetail setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
		return this;
	}
	public Double getDiscount() {
		return discount;
	}
	public TediDetail setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
}
