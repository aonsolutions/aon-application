package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;

public class BookingCheck implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;

	private Customer customer;
	private OldItem item;
	
	private RegistryMode type;
	private RegistryItemStatus status;
	
	private String quantity;
	private Double price;
	private String discountExpr;
	
	private Date startDate;
	private Date endDate;
	
	private Workplace workplace;
	
	private boolean hasFee;
	
	private String quantityFee;
	private String quantityRItem;
	
	public Integer getId() {
		return id;
	}
	
	public BookingCheck setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		if(domain == null) {
			domain = new Domain();
		}
		return domain;
	}
	
	public BookingCheck setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public Customer getCustomer() {
		if(customer == null) {
			customer = new Customer();
		}
		return customer;
	}
	
	public BookingCheck setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public OldItem getItem() {
		if(item == null) {
			item = new OldItem();
		}
		return item;
	}
	
	public BookingCheck setItem(OldItem item) {
		this.item = item;
		return this;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public BookingCheck setQuantity(String quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public BookingCheck setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public BookingCheck setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public RegistryMode getType() {
		return type;
	}

	public BookingCheck setType(RegistryMode type) {
		this.type = type;
		return this;
	}

	public RegistryItemStatus getStatus() {
		return status;
	}

	public BookingCheck setStatus(RegistryItemStatus status) {
		this.status = status;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public BookingCheck setPrice(Double price) {
		this.price = price;
		return this;
	}

	public String getDiscountExpr() {
		return discountExpr;
	}

	public BookingCheck setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public BookingCheck setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}

	public boolean hasFee() {
		return hasFee;
	}

	public BookingCheck setHasFee(boolean hasFee) {
		this.hasFee = hasFee;
		return this;
	}

	public String getQuantityFee() {
		return quantityFee;
	}

	public BookingCheck setQuantityFee(String quantityFee) {
		this.quantityFee = quantityFee;
		return this;
	}

	public String getQuantityRItem() {
		return quantityRItem;
	}

	public BookingCheck setQuantityRItem(String quantityRItem) {
		this.quantityRItem = quantityRItem;
		return this;
	}

	
	
}
