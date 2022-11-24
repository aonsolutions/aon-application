package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;

public class DeliveryDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3053139577316342602L;
	private Integer id;
	private int domain;
	private Delivery delivery;
	private short line;
	private Item item;
	private String description;
	private Integer warehouse;
	private double quantity;
	private double price;
	private String discountExpression;
	private Integer salesDetail;
	private SalesDetail salesDetailData;
	private String purchaseReference;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	public DeliveryDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public DeliveryDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Delivery getDelivery() {
		if(delivery == null) 
			delivery = new Delivery();
		return delivery;
	}
	public DeliveryDetail setDelivery(Delivery delivery) {
		this.delivery = delivery;
		return this;
	}
	public short getLine() {
		return line;
	}
	public DeliveryDetail setLine(short line) {
		this.line = line;
		return this;
	}
	public Item getItem() {
		if(item == null) 
			item = new Item();
		return item;
	}
	public DeliveryDetail setItem(Item item) {
		this.item = item;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public DeliveryDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public DeliveryDetail setPrice(double price) {
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public DeliveryDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public DeliveryDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public DeliveryDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public DeliveryDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public DeliveryDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getQuantity() {
		return quantity;
	}
	public DeliveryDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Integer getSalesDetail() {
		return salesDetail;
	}
	public DeliveryDetail setSalesDetail(Integer salesDetail) {
		this.salesDetail = salesDetail;
		return this;
	}
	
	public SalesDetail getSalesDetailData() {
		return salesDetailData;
	}
	
	public DeliveryDetail setSalesDetailData(SalesDetail salesDetailData) {
		this.salesDetailData = salesDetailData;
		return this;
	}
	
	public String getPurchaseReference() {
		return purchaseReference;
	}
	public DeliveryDetail setPurchaseReference(String purchaseReference) {
		this.purchaseReference = purchaseReference;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public DeliveryDetail setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	
	@Deprecated
	public String getProductCode() {
		return getItem().getProduct().getCode();
	}
	
	@Deprecated
	public DeliveryDetail setProductCode(String productCode) {
		getItem().getProduct().setCode(productCode);
		return this;
	}
	
	@Deprecated
	public String getProductName() {
		return getItem().getProduct().getName();
	}
	
	@Deprecated
	public DeliveryDetail setProductName(String productName) {
		getItem().getProduct().setName(productName);
		return this;
	}
	
	@Deprecated
	public Integer getProductId() {
		return getItem().getProduct().getId();
	}
	
	@Deprecated
	public DeliveryDetail setProductId(Integer productId) {
		getItem().getProduct().setId(productId);
		return this;
	}	

}
