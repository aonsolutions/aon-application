package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Item implements Serializable {

	private static final long serialVersionUID = 817004609996847321L;
	
	private Integer id;
	private Domain domain;
	private String detail;
	private String detail2;
	private String detail3;
	private String description;
	private String serialNumber;
	private Date serialDate;
	private Date expireDate;
	private String barcode;
	
	private ProductStatus status;
	private Product product;
	
	private double price;
	private double expensesPercent;
	private double expensesFixed;
	private double profitPercent;
	private double purchasePrice;
	private boolean internet;
	
	private Tag packFormatTag;
	private Integer packUnits;
	private Tag packUnitsTag;
 	private double packMeasurement;
	private Tag packMeasurementTag;
	private Tag stockUnitTag;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private List<ItemComposition> itemComposition;
	
	private boolean removed;
 
	public Integer getId() { 
		return id;
	}
	
	public Item setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public Item setDomain(Domain domain) {
		this.domain = domain;
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

	public String getSerialNumber() {
		return serialNumber;
	}
	
	public Item setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public Date getSerialDate() {
		return serialDate;
	}
	
	public Item setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
		return this;
	}

	public Date getExpireDate() {
		return expireDate;
	}
	
	public Item setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
		return this;
	}

	public String getBarcode() {
		return barcode;
	}
	
	public Item setBarcode(String barcode) {
		this.barcode = barcode;
		return this;
	}
	
	public Boolean isActive() {
		return ProductStatus.ACTIVE.equals(getStatus());
	}
	
	public ProductStatus getStatus() {
		return status;
	}
	
	public Item setStatus(ProductStatus status) {
		this.status = status;
		return this;
	}

	public double getPrice() {
		return price;
	}
	
	public Item setPrice(double price) {
		this.price = price;
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

	public Tag getPackFormatTag() {
		if(packFormatTag == null) {
			packFormatTag = new Tag();
		}
		return packFormatTag;
	}
	
	public Item setPackFormatTag(Tag packFormatTag) {
		this.packFormatTag = packFormatTag;
		return this;
	}

	public Integer getPackUnits() {
		return packUnits;
	}
	
	public Item setPackUnits(Integer packUnits) {
		this.packUnits = packUnits;
		return this;
	}

	public Tag getPackUnitsTag() {
		if(packUnitsTag == null)
			packUnitsTag = new Tag();
		return packUnitsTag;
	}
	
	public Item setPackUnitsTag(Tag packUnitsTag) {
		this.packUnitsTag = packUnitsTag;
		return this;
	}

	public double getPackMeasurement() {
		return packMeasurement;
	}
	
	public Item setPackMeasurement(double packMeasurement) {
		this.packMeasurement = packMeasurement;
		return this;
	}

	public Tag getPackMeasurementTag() {
		if(packMeasurementTag == null)
			packMeasurementTag = new Tag();
		return packMeasurementTag;
	}
	
	public Item setPackMeasurementTag(Tag packMeasurementTag) {
		this.packMeasurementTag = packMeasurementTag;
		return this;
	}

	public Tag getStockUnitTag() {
		if(stockUnitTag == null)
			stockUnitTag = new Tag();
		return stockUnitTag;
	}
	
	public Item setStockUnitTag(Tag stockUnitTag) {
		this.stockUnitTag = stockUnitTag;
		return this;
	}

	public Product getProduct() {
		if(product == null) {
			product = new Product();
		}
		return product;
	}
	
	public Item setProduct(Product product) {
		this.product = product;
		return this;
	}
	
	public List<ItemComposition> getItemComposition() {
		if(itemComposition == null) itemComposition = new LinkedList<>();
		return itemComposition;
	}
	
	public Item setItemComposition(List<ItemComposition> itemComposition) {
		this.itemComposition = itemComposition;
		return this;
	}
	
	public Item addItemComposition(ItemComposition itemComposition) {
		getItemComposition().add(itemComposition);
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public Item setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public Item setCreationDate(Date creationDate) {
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

	public Date getModificationDate() {
		return modificationDate;
	}
	
	public Item setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
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
		sb.append(getProduct().getName());
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
	
	public boolean isRemoved() {
		return removed;
	}
	
	public Item setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDetail() == null && getDetail2() == null 
				&& getDetail3() == null && getDescription() == null && getSerialNumber() == null
				&& getSerialDate() == null && getBarcode() == null;
	}
	
	public Item copy() {
		return new Item()
		.setDomain(getDomain())
		.setDetail(getDetail())
		.setDetail2(getDetail2())
		.setDetail3(getDetail3())
		.setDescription(getDescription())
		.setSerialNumber(getSerialNumber())
		.setSerialDate(getSerialDate())
		.setBarcode(getBarcode())
		.setStatus(getStatus())
		.setProduct(getProduct())
		.setPrice(getPrice())
		.setExpensesPercent(getExpensesPercent())
		.setProfitPercent(getProfitPercent())
		.setPurchasePrice(getPurchasePrice())
		.setExpensesFixed(getExpensesFixed())
		.setInternet(isInternet())
		.setPackFormatTag(getPackFormatTag())
		.setPackUnits(getPackUnits())
		.setPackUnitsTag(getPackUnitsTag())
		.setPackMeasurement(getPackMeasurement())
		.setPackMeasurementTag(getPackMeasurementTag())
		.setStockUnitTag(getStockUnitTag());
	}
}
