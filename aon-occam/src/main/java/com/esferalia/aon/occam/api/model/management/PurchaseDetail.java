package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;

public class PurchaseDetail implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4155547176182716960L;
	private Integer id;
	private int domain;
	private int purchaseId;
	private Purchase purchase;
	private Integer project;
	private int item;
	private Integer line;
	private String description;
	private double quantity;
	private double price;
	private String discountExpression;
	private double taxes;
	private PurchaseDetailStatus status;
	private Integer proposalDetail;
	private PurchaseSourceType source;
	private Integer sourceId;
	private double delivered;
	private Date deliveryDate; 
	
	private Date creationDate; 
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	private Integer productId;
	private String productCode;
	private String productName;
	
	private Integer carrier;
	private Integer carrierPacking;
	
	public Integer getId() {
		return id;
	}
	public PurchaseDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public PurchaseDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public PurchaseDetail setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
		return this;
	}
	public Purchase getPurchase() {
		return purchase;
	}
	public PurchaseDetail setPurchase(Purchase purchase) {
		this.purchase = purchase;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public PurchaseDetail setProject(Integer project) {
		this.project = project;
		return this;
	}
	public int getItem() {
		return item;
	}
	public PurchaseDetail setItem(int item) {
		this.item = item;
		return this;
	}
	public Integer getLine() {
		return line;
	}
	public PurchaseDetail setLine(Integer integer) {
		this.line = integer;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public PurchaseDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public PurchaseDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public PurchaseDetail setPrice(double price) {
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public PurchaseDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public double getTaxes() {
		return taxes;
	}
	public PurchaseDetail setTaxes(double taxes) {
		this.taxes = taxes;
		return this;
	}
	public PurchaseDetailStatus getStatus() {
		return status;
	}
	public PurchaseDetail setStatus(PurchaseDetailStatus status) {
		this.status = status;
		return this;
	}
	public Integer getProposalDetail() {
		return proposalDetail;
	}
	public PurchaseDetail setProposalDetail(Integer proposalDetail) {
		this.proposalDetail = proposalDetail;
		return this;
	}
	public double getDelivered() {
		return delivered;
	}
	public PurchaseDetail setDelivered(double delivered) {
		this.delivered = delivered;
		return this;
	}
	public PurchaseSourceType getSource() {
		return source;
	}
	public PurchaseDetail setSource(PurchaseSourceType source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public PurchaseDetail setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	public String getProductCode() {
		return productCode;
	}
	public PurchaseDetail setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}
	public String getProductName() {
		return productName;
	}
	public PurchaseDetail setProductName(String productName) {
		this.productName = productName;
		return this;
	}
	public Integer getCarrierPacking(){
		return carrierPacking;
	}
	public PurchaseDetail setCarrierPacking(Integer carrierPacking){
		this.carrierPacking = carrierPacking;
		return this;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}
	public Integer getCarrier() {
		return carrier;
	}
	public void setCarrier(Integer carrier) {
		this.carrier = carrier;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	public String toJSON(){
		return "{}";
	}
}
