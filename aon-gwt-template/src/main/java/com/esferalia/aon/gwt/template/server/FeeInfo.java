package com.esferalia.aon.gwt.template.server;

import java.util.Date;

import com.esferalia.aon.occam.api.model.fee.Fee;

public class FeeInfo {

	String client;
	Integer clientId;
	String product;
	Double quantity;
	Date startDate;
	Date endDate;
	Date billingDate;
	Integer period;
	Double price;
	Double discount;
	String seller;
	Integer sellerId;
	String workplace;
	Integer workplaceId;
	Integer billingGroup;
	Boolean confidential;
	String project;
	Integer projectId;
	String detail;
	String detail2;
	String detail3;
	Integer row;
	String description;
	Integer line;
	
	Fee fee;
	
	public FeeInfo() {

	}
	
	public Fee getFee() {
		return fee;
	}
	
	public void setFee(Fee fee) {
		this.fee = fee;
	}
	
	public String getClient() {
		return client;
	}
	
	public FeeInfo setClient(String client) {
		this.client = client;
		return this;
	}
	
	public String getProduct() {
		return product;
	}
	
	public FeeInfo setProduct(String product) {
		this.product = product;
		return this;
	}
	
	public Double getQuantity() {
		return quantity;
	}
	
	public FeeInfo setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public FeeInfo setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public FeeInfo setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Date getBillingDate() {
		return billingDate;
	}
	
	public FeeInfo setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
		return this;
	}
	
	public Integer getPeriod() {
		return period;
	}
	
	public FeeInfo setPeriod(Integer period) {
		this.period = period;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public FeeInfo setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public Double getDiscount() {
		return discount;
	}
	
	public FeeInfo setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
	
	public String getSeller() {
		return seller;
	}
	
	public FeeInfo setSeller(String seller) {
		this.seller = seller;
		return this;
	}
	
	public Integer getSellerId() {
		return sellerId;
	}
	
	public FeeInfo setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
		return this;
	}
	
	public String getWorkplace() {
		return workplace;
	}
	
	public FeeInfo setWorkplace(String workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	
	public FeeInfo setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}
	
	public Integer getBillingGroup() {
		return billingGroup;
	}
	
	public FeeInfo setBillingGroup(Integer billingGroup) {
		this.billingGroup = billingGroup;
		return this;
	}
	
	public Boolean getConfidential() {
		return confidential;
	}
	
	public FeeInfo setConfidential(Boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	
	public String getProject() {
		return project;
	}
	
	public FeeInfo setProject(String project) {
		this.project = project;
		return this;
	}
	
	public Integer getProjectId() {
		return projectId;
	}
	
	public FeeInfo setProjectId(Integer projectId) {
		this.projectId = projectId;
		return this;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public FeeInfo setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	
	public String getDetail2() {
		return detail2;
	}
	
	public FeeInfo setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	
	public String getDetail3() {
		return detail3;
	}
	
	public FeeInfo setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	
	public Integer getRow() {
		return row;
	}
	
	public FeeInfo setRow(Integer row) {
		this.row = row;
		return this;
	}
	
	public Integer getClientId() {
		return clientId;
	}
	
	public FeeInfo setClientId(Integer clientId) {
		this.clientId = clientId;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public FeeInfo setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Integer getLine() {
		return line;
	}
	
	public FeeInfo setLine(Integer line) {
		this.line = line;
		return this;
	}
	
	public FeeInfo setLine(Double line) {
		this.line = line.intValue();
		return this;
	}
	
	public Boolean isEmpty() {
		return getClient() == null && getProduct() == null && getQuantity() == null
			&& getStartDate() == null && getEndDate() == null && getBillingDate() == null
			&& getPeriod() == null && getPrice() == null && getDiscount() == null
			&& getSeller() == null && getWorkplace() == null && getBillingGroup() == null
			&& getConfidential() == null && getProject() == null && getDetail() == null
			&& getDetail2() == null && getDetail3() == null && getRow() == null
			&& getDescription() == null && getLine() == null;
	}
	
}
