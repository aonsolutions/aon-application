package com.esferalia.aon.occam.api.model.warehouse;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.product.Product;

public class SerfruitDeliveryPackaging {
	
	Product product;
	String sscc;
	List<SerfruitDeliveryPackaging> content;
	Integer deliveryLine;
	double quantity;
	
	public Product getProduct() {
		return product;
	}
	
	public SerfruitDeliveryPackaging setProduct(Product product) {
		this.product = product;
		return this;
	}
	
	public String getSscc() {
		return sscc;
	}
	
	public SerfruitDeliveryPackaging setSscc(String sscc) {
		this.sscc = sscc;
		return this;
	}
	
	public List<SerfruitDeliveryPackaging> getContent() {
		if(content == null) content = new LinkedList<>();
		return content;
	}
	
	public SerfruitDeliveryPackaging setContent(List<SerfruitDeliveryPackaging> content) {
		this.content = content;
		return this;
	}
	
	public SerfruitDeliveryPackaging addContent(SerfruitDeliveryPackaging deliveryPackaging) {
		getContent().add(deliveryPackaging);
		return this;
	}
	
	public Integer getDeliveryLine() {
		return deliveryLine;
	}
	
	public SerfruitDeliveryPackaging setDeliveryLine(Integer deliveryLine) {
		this.deliveryLine = deliveryLine;
		return this;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public SerfruitDeliveryPackaging setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
}
