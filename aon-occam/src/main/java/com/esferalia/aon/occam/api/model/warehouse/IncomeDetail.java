package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Project;

public class IncomeDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private int domain;
	private Income income;
	private Project project;
	private short line;
	private Item item;
	private String description;
	private Integer warehouse;
	private double quantity;
	private double price;
	private String discountExpression;
	private Integer purchaseDetail;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
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
	public Income getIncome() {
		return income;
	}
	public void setIncome(Income income) {
		this.income = income;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public short getLine() {
		return line;
	}
	public void setLine(short line) {
		this.line = line;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getPrice() {
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public Integer getPurchaseDetail() {
		return purchaseDetail;
	}
	public void setPurchaseDetail(Integer purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
	}
	
	

}
