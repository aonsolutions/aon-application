package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.istack.NotNull;

@Deprecated
public class OldItem implements Serializable {

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
	private Date serialDate;
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	private OldProduct product;

	private Tag packFormatTag;
	private Double packUnits;
	private Tag packUnitsTag;
 	private Double packMeasurement;
	private Tag packMeasurementTag;
	private Tag stockUnitTag;
	
	public Integer getId() { 
		return id;
	}
	public OldItem setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public OldItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProductId() {
		return productId;
	}
	public OldItem setProductId(Integer productId) {
		this.productId = productId;
		return this;
	}
	public Account getSalesAccount() {
		return salesAccount;
	}
	public OldItem setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public OldItem setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	public Tax getVat() {
		return vat;
	}
	public OldItem setVat(Tax vat) {
		this.vat = vat;
		return this;
	}
	public String getCategory() {
		return category;
	}
	public OldItem setCategory(String category) {
		this.category = category;
		return this;
	}
	public Tax getRetention() {
		return retention;
	}
	public OldItem setRetention(Tax retention) {
		this.retention = retention;
		return this;
	}
	public String getBrand() {
		return brand;
	}
	public OldItem setBrand(String brand) {
		this.brand = brand;
		return this;
	}
	public String getName() {
		return name;
	}
	public OldItem setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public OldItem setCode(String code) {
		this.code = code;
		return this;
	}
	public boolean isInventoriable() {
		return inventoriable;
	}
	public OldItem setInventoriable(boolean inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}
	public ProductType getType() {
		return type;
	}
	public OldItem setType(ProductType type) {
		this.type = type;
		return this;
	}
	public boolean isComposition() {
		return composition;
	}
	public OldItem setComposition(boolean composition) {
		this.composition = composition;
		return this;
	}
	public boolean isCompositionPrice() {
		return compositionPrice;
	}
	public OldItem setCompositionPrice(boolean compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}
	public String getDetail() {
		return detail;
	}
	public OldItem setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public OldItem setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public OldItem setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public OldItem setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public OldItem setPrice(double price) {
		this.price = price;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public OldItem setActive(boolean active) {
		this.active = active;
		return this;
	}
	public double getExpensesPercent() {
		return expensesPercent;
	}
	public OldItem setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
		return this;
	}
	public double getExpensesFixed() {
		return expensesFixed;
	}
	public OldItem setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
		return this;
	}
	public double getProfitPercent() {
		return profitPercent;
	}
	public OldItem setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
		return this;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public OldItem setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
		return this;
	}
	public boolean isInternet() {
		return internet;
	}
	public OldItem setInternet(boolean internet) {
		this.internet = internet;
		return this;
	}
	public String getBarcode() {
		return barcode;
	}
	public OldItem setBarcode(String barcode) {
		this.barcode = barcode;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public OldItem setStatus(Byte status) {
		this.status = status;
		return this;
	}
	
	
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public OldItem setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public Date getSerialDate() {
		return serialDate;
	}
	public OldItem setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
		return this;
	}
	
	
	
	public OldProduct getProduct() {
		if(product == null) {
			product = new OldProduct();
		}
		return product;
	}
	public OldItem setProduct(OldProduct product) {
		this.product = product;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public OldItem setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public OldItem setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public OldItem setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public OldItem setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
		
	public @NotNull Tag getPackFormatTag() {
		return packFormatTag != null ? packFormatTag : new Tag();
	}
	public OldItem setPackFormatTag(Tag packFormatTag) {
		this.packFormatTag = packFormatTag;
		return this;
	}
	public Double getPackUnits() {
		return packUnits != null ? packUnits : 0;
	}
	public OldItem setPackUnits(Double packUnits) {
		this.packUnits = packUnits;
		return this;
	}
	public @NotNull Tag getPackUnitsTag() {
		return packUnitsTag != null ? packUnitsTag : new Tag();
	}
	public OldItem setPackUnitsTag(Tag packUnitsTag) {
		this.packUnitsTag = packUnitsTag;
		return this;
	}
	public Double getPackMeasurement() {
		return packMeasurement != null ? packMeasurement : 0;
	}
	public OldItem setPackMeasurement(Double packMeasurement) {
		this.packMeasurement = packMeasurement;
		return this;
	}
	public @NotNull Tag getPackMeasurementTag() {
		return packMeasurementTag != null ? packMeasurementTag : new Tag();
	}
	public OldItem setPackMeasurementTag(Tag packMeasurementTag) {
		this.packMeasurementTag = packMeasurementTag;
		return this;
	}
	
	public @NotNull Tag getStockUnitTag() {
		return stockUnitTag != null ? stockUnitTag : new Tag();
	}
	public OldItem setStockUnitTag(Tag stockUnitTag) {
		this.stockUnitTag = stockUnitTag;
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
