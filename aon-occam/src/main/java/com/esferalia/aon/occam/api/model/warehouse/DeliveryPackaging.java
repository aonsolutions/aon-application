package com.esferalia.aon.occam.api.model.warehouse;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.product.Product;

public class DeliveryPackaging {
	
	Product product;
	String sscc;
	List<DeliveryPackaging> content;
	Integer deliveryLine;
	double quantity;
	
	public Product getProduct() {
		return product;
	}
	
	public DeliveryPackaging setProduct(Product product) {
		this.product = product;
		return this;
	}
	
	public String getSscc() {
		return sscc;
	}
	
	public DeliveryPackaging setSscc(String sscc) {
		this.sscc = sscc;
		return this;
	}
	
	public List<DeliveryPackaging> getContent() {
		if(content == null) content = new LinkedList<>();
		return content;
	}
	
	public DeliveryPackaging setContent(List<DeliveryPackaging> content) {
		this.content = content;
		return this;
	}
	
	public DeliveryPackaging addContent(DeliveryPackaging deliveryPackaging) {
		getContent().add(deliveryPackaging);
		return this;
	}
	
	public Integer getDeliveryLine() {
		return deliveryLine;
	}
	
	public DeliveryPackaging setDeliveryLine(Integer deliveryLine) {
		this.deliveryLine = deliveryLine;
		return this;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public DeliveryPackaging setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
}
