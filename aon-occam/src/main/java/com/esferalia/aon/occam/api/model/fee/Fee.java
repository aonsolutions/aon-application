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
	private Integer customer;
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
	public Fee setClient(String client) {
		this.client = client;
		return this;
	}
	public String getProduct() {
		return product;
	}
	public Fee setProduct(String product) {
		this.product = product;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public Fee setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Fee setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public Fee setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public Date getBillingDate() {
		return billingDate;
	}
	public Fee setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
		return this;
	}
	public Short getPeriod() {
		return period;
	}
	public Fee setPeriod(Short period) {
		this.period = period;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public Fee setPrice(Double price) {
		this.price = price;
		return this;
	}
	public Double getDiscount() {
		return discount;
	}
	public Fee setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
	public String getSeller() {
		return seller;
	}
	public Fee setSeller(String seller) {
		this.seller = seller;
		return this;
	}
	public Integer getSellerId() {
		return sellerId;
	}
	public Fee setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
		return this;
	}
	public String getWorkplace() {
		return workplace;
	}
	public Fee setWorkplace(String workplace) {
		this.workplace = workplace;
		return this;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public Fee setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}
	public Integer getBillingGroup() {
		return billingGroup;
	}
	public Fee setBillingGroup(Integer billingGroup) {
		this.billingGroup = billingGroup;
		return this;
	}
	public Boolean getConfidential() {
		return confidential;
	}
	public Fee setConfidential(Boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	public String getProject() {
		return project;
	}
	public Fee setProject(String project) {
		this.project = project;
		return this;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public Fee setProjectId(Integer projectId) {
		this.projectId = projectId;
		return this;
	}
	public String getDetail() {
		return detail;
	}
	public Fee setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public Fee setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public Fee setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	public Integer getRow() {
		return row;
	}
	public Fee setRow(Integer row) {
		this.row = row;
		return this;
	}
	public Integer getClientId() {
		return clientId;
	}
	public Fee setClientId(Integer clientId) {
		this.clientId = clientId;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Fee setDescription(String description) {
		this.description = description;
		return this;
	}
	public Short getLine() {
		return line;
	}
	public Fee setLine(Short line) {
		this.line = line;
		return this;
	}
	public Fee setLine(Double line) {
		this.line = line.shortValue();
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Fee setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Fee setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getItemId() {
		return itemId;
	}
	public Fee setItemId(Integer itemId) {
		this.itemId = itemId;
		return this;
	}
	public String getDiscountExpr() {
		return discountExpr;
	}
	public Fee setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}
	public Byte getSecurityLevel() {
		return securityLevel;
	}
	public Fee setSecurityLevel(Byte securityLevel){
		this.securityLevel = securityLevel;
		return this;
	}
	public Integer getCustomer() {
		return customer;
	}
	public Fee setCustomer(Integer customer) {
		this.customer = customer;
		return this;
	}
	
	
	
}
