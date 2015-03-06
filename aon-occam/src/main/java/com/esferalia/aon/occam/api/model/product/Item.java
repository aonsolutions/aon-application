package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Item implements Serializable {

	private static final long serialVersionUID = 817004609996847321L;
	
	private Integer id;
	private int domain;
	private Integer productId;
	private Account salesAccount;
	private Account purchaseAccount;
	private Tax vat;
	private String category;
	private Tax retention;
	private String brand;
	private String name;
	private String code;
	private boolean inventoriable;
	private ProductType type;
	private boolean composition;
	private boolean compositionPrice;
	private String detail;
	private String detail2;
	private String detail3;
	private String description;
	private double price;
	private boolean active;
	private double expensesPercent;
	private double expensesFixed;
	private double profitPercent;
	private double purchasePrice;
	private boolean internet;
	private String barcode;
	
	public Integer getId() {
		return id;
	}
	public Item setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Item setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProductId() {
		return productId;
	}
	public Item setProductId(Integer productId) {
		this.productId = productId;
		return this;
	}
	public Account getSalesAccount() {
		return salesAccount;
	}
	public Item setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public Item setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	public Tax getVat() {
		return vat;
	}
	public Item setVat(Tax vat) {
		this.vat = vat;
		return this;
	}
	public String getCategory() {
		return category;
	}
	public Item setCategory(String category) {
		this.category = category;
		return this;
	}
	public Tax getRetention() {
		return retention;
	}
	public Item setRetention(Tax retention) {
		this.retention = retention;
		return this;
	}
	public String getBrand() {
		return brand;
	}
	public Item setBrand(String brand) {
		this.brand = brand;
		return this;
	}
	public String getName() {
		return name;
	}
	public Item setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Item setCode(String code) {
		this.code = code;
		return this;
	}
	public boolean isInventoriable() {
		return inventoriable;
	}
	public Item setInventoriable(boolean inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}
	public ProductType getType() {
		return type;
	}
	public Item setType(ProductType type) {
		this.type = type;
		return this;
	}
	public boolean isComposition() {
		return composition;
	}
	public Item setComposition(boolean composition) {
		this.composition = composition;
		return this;
	}
	public boolean isCompositionPrice() {
		return compositionPrice;
	}
	public Item setCompositionPrice(boolean compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}
	public String getDetail() {
		return detail;
	}
	public Item setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public Item setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public Item setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Item setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public Item setPrice(double price) {
		this.price = price;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public Item setActive(boolean active) {
		this.active = active;
		return this;
	}
	public double getExpensesPercent() {
		return expensesPercent;
	}
	public Item setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
		return this;
	}
	public double getExpensesFixed() {
		return expensesFixed;
	}
	public Item setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
		return this;
	}
	public double getProfitPercent() {
		return profitPercent;
	}
	public Item setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
		return this;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public Item setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
		return this;
	}
	public boolean isInternet() {
		return internet;
	}
	public Item setInternet(boolean internet) {
		this.internet = internet;
		return this;
	}
	public String getBarcode() {
		return barcode;
	}
	public Item setBarcode(String barcode) {
		this.barcode = barcode;
		return this;
	}

	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		if (AonStringUtils.isNotEmpty(getDetail()) || AonStringUtils.isNotEmpty(getDetail2()) || AonStringUtils.isNotEmpty(getDetail3())) {
			sb.append(' ');
			sb.append('[');
			if (AonStringUtils.isNotEmpty(getDetail())) {
				sb.append(getDetail());
			}
			if (AonStringUtils.isNotEmpty(getDetail2())) {
				if ( sb.length() > 0 ) {
					sb.append("/");
				}
				sb.append(getDetail2());
			}
			if (AonStringUtils.isNotEmpty(getDetail3())) {
				if ( sb.length() > 0 ) {
					sb.append("/");
				}
				sb.append(getDetail3());
			}
			sb.append(']');
		}
		return sb.toString();
	}
	
}
