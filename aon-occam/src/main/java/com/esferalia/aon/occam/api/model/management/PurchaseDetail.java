package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;

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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	public Purchase getPurchase() {
		return purchase;
	}
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	public Integer getProject() {
		return project;
	}
	public void setProject(Integer project) {
		this.project = project;
	}
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public Integer getLine() {
		return line;
	}
	public void setLine(Integer integer) {
		this.line = integer;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public void setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
	}
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	public PurchaseDetailStatus getStatus() {
		return status;
	}
	public void setStatus(PurchaseDetailStatus status) {
		this.status = status;
	}
	public Integer getProposalDetail() {
		return proposalDetail;
	}
	public void setProposalDetail(Integer proposalDetail) {
		this.proposalDetail = proposalDetail;
	}
	public double getDelivered() {
		return delivered;
	}
	public void setDelivered(double delivered) {
		this.delivered = delivered;
	}
	public PurchaseSourceType getSource() {
		return source;
	}
	public void setSource(PurchaseSourceType source) {
		this.source = source;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	
}
