package com.esferalia.aon.occam.api.model.fee;

import java.io.Serializable;
import java.util.Date;

public class Fee implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1524715028531794556L;
	
	
	private Integer id;
	private String client;
	private Integer clientId;
	private Integer domain;
	private String product;
	private Double quantity;
	private Date startDate;
	private Date endDate;
	private Date billingDate;
	private Short period;
	private Double price;
	private Double discount;
	private String discountExpr;
	private String seller;
	private Integer sellerId;
	private String workplace;
	private Integer workplaceId;
	private Integer billingGroup;
	private Boolean confidential;
	private String project;
	private Integer projectId;
	private String detail;
	private String detail2;
	private String detail3;
	private Integer row;
	private String description;
	private Short line;
	private Integer itemId;
	private Byte securityLevel;
	
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getBillingDate() {
		return billingDate;
	}
	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}
	public Short getPeriod() {
		return period;
	}
	public void setPeriod(Short period) {
		this.period = period;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public Integer getSellerId() {
		return sellerId;
	}
	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}
	public String getWorkplace() {
		return workplace;
	}
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	public Integer getBillingGroup() {
		return billingGroup;
	}
	public void setBillingGroup(Integer billingGroup) {
		this.billingGroup = billingGroup;
	}
	public Boolean getConfidential() {
		return confidential;
	}
	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDetail2() {
		return detail2;
	}
	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
	public String getDetail3() {
		return detail3;
	}
	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Short getLine() {
		return line;
	}
	public void setLine(Short line) {
		this.line = line;
	}
	public void setLine(Double line) {
		this.line = line.shortValue();
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getDiscountExpr() {
		return discountExpr;
	}
	public void setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
	}
	public Byte getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(Byte securityLevel){
		this.securityLevel = securityLevel;
	}
	
	
}
