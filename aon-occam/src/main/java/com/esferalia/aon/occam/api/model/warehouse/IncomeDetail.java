package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Project;

public class IncomeDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private int domain;
	private Income income;
	private Project project;
	private short line;
	private OldItem item;
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
	public IncomeDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public IncomeDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Income getIncome() {
		return income;
	}
	public IncomeDetail setIncome(Income income) {
		this.income = income;
		return this;
	}
	public Project getProject() {
		return project;
	}
	public IncomeDetail setProject(Project project) {
		this.project = project;
		return this;
	}
	public Short getLine() {
		return line;
	}
	public IncomeDetail setLine(short line) {
		this.line = line;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public IncomeDetail setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public IncomeDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public IncomeDetail setPrice(double price) {
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public IncomeDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public IncomeDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public IncomeDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public IncomeDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public IncomeDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Double getQuantity() {
		return quantity;
	}
	public IncomeDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Integer getPurchaseDetail() {
		return purchaseDetail;
	}
	public IncomeDetail setPurchaseDetail(Integer purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public IncomeDetail setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
}
