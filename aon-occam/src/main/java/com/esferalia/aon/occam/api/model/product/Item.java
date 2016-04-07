package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.istack.NotNull;

public class Item implements Serializable {

	private static final long serialVersionUID = 817004609996847321L;
	
	private Integer id;
	private Integer domain;
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
	private Byte status;
	private String serialNumber;
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	private Product product;

	private Tag packFormatTag;
	private Double packUnits;
	private Tag packUnitsTag;
 	private Double packMeasurement;
	private Tag packMeasurementTag;
	
	public Integer getId() { 
		return id;
	}
	public Item setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Item setDomain(Integer domain) {
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
	public Byte getStatus() {
		return status;
	}
	public Item setStatus(Byte status) {
		this.status = status;
		return this;
	}
	
	
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public Item setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}
	
	
	
	public Product getProduct() {
		return product;
	}
	public Item setProduct(Product product) {
		this.product = product;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Item setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Item setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Item setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Item setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
		
	public @NotNull Tag getPackFormatTag() {
		return packFormatTag != null ? packFormatTag : new Tag();
	}
	public Item setPackFormatTag(Tag packFormatTag) {
		this.packFormatTag = packFormatTag;
		return this;
	}
	public Double getPackUnits() {
		return packUnits != null ? packUnits : 0;
	}
	public Item setPackUnits(Double packUnits) {
		this.packUnits = packUnits;
		return this;
	}
	public @NotNull Tag getPackUnitsTag() {
		return packUnitsTag != null ? packUnitsTag : new Tag();
	}
	public Item setPackUnitsTag(Tag packUnitsTag) {
		this.packUnitsTag = packUnitsTag;
		return this;
	}
	public Double getPackMeasurement() {
		return packMeasurement != null ? packMeasurement : 0;
	}
	public Item setPackMeasurement(Double packMeasurement) {
		this.packMeasurement = packMeasurement;
		return this;
	}
	public @NotNull Tag getPackMeasurementTag() {
		return packMeasurementTag != null ? packMeasurementTag : new Tag();
	}
	public Item setPackMeasurementTag(Tag packMeasurementTag) {
		this.packMeasurementTag = packMeasurementTag;
		return this;
	}
	
	public String getDetails(){
		String details = "";
		if(getDetail() != null)
			details.concat(getDetail());
		if(getDetail2() != null)
			details.concat(", "+getDetail2());
		if(getDetail3() != null)
			details.concat(", "+getDetail3());
		return details;
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
