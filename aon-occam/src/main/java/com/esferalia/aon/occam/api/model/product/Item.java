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
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Account getSalesAccount() {
		return salesAccount;
	}
	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}
	public Tax getVat() {
		return vat;
	}
	public void setVat(Tax vat) {
		this.vat = vat;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Tax getRetention() {
		return retention;
	}
	public void setRetention(Tax retention) {
		this.retention = retention;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isInventoriable() {
		return inventoriable;
	}
	public void setInventoriable(boolean inventoriable) {
		this.inventoriable = inventoriable;
	}
	public ProductType getType() {
		return type;
	}
	public void setType(ProductType type) {
		this.type = type;
	}
	public boolean isComposition() {
		return composition;
	}
	public void setComposition(boolean composition) {
		this.composition = composition;
	}
	public boolean isCompositionPrice() {
		return compositionPrice;
	}
	public void setCompositionPrice(boolean compositionPrice) {
		this.compositionPrice = compositionPrice;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public double getExpensesPercent() {
		return expensesPercent;
	}
	public void setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
	}
	public double getExpensesFixed() {
		return expensesFixed;
	}
	public void setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
	}
	public double getProfitPercent() {
		return profitPercent;
	}
	public void setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public boolean isInternet() {
		return internet;
	}
	public void setInternet(boolean internet) {
		this.internet = internet;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
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
