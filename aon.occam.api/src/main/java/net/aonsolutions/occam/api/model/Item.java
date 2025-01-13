package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.metadata.ItemMetadata;
import net.aonsolutions.occam.api.model.type.ProductStatus;

public class Item extends AonEntity<ItemMetadata> implements HasAudit {

	private static final long serialVersionUID = 817004609996847321L;
	
	private Integer id;
	private Integer domain;
	private Product product;
	private String detail;
	private String detail2;
	private String detail3;
	private String description;
	private String serialNumber;
	private Date serialDate;
	private Date expireDate;
	private String barcode;
	private ProductStatus status;
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
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
 
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Item markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Item setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public Item setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}

	public Integer getId() { 
		return id;
	}
	public Item setId(Integer id) {
		checkIfDirty( this.id,id, ItemMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Item setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, ItemMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public Product getProduct() {
		return product;
	}
	public Item setProduct(Product product) {
		checkIfDirty( this.product,product, ItemMetadata.PRODUCT);
		this.product = product;
		return this;
	}
	
	public String getDetail() {
		return detail;
	}
	public Item setDetail(String detail) {
		checkIfDirty( this.detail,detail, ItemMetadata.DETAIL);
		this.detail = detail;
		return this;
	}
	
	public String getDetail2() {
		return detail2;
	}
	public Item setDetail2(String detail2) {
		checkIfDirty( this.detail2,detail2, ItemMetadata.DETAIL2);
		this.detail2 = detail2;
		return this;
	}
	
	public String getDetail3() {
		return detail3;
	}
	public Item setDetail3(String detail3) {
		checkIfDirty( this.detail3,detail3, ItemMetadata.DETAIL3);
		this.detail3 = detail3;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public Item setDescription(String description) {
		checkIfDirty( this.description,description, ItemMetadata.DESCRIPTION);
		this.description = description;
		return this;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public Item setSerialNumber(String serialNumber) {
		checkIfDirty( this.serialNumber,serialNumber, ItemMetadata.SERIAL_NUMBER);
		this.serialNumber = serialNumber;
		return this;
	}

	public Date getSerialDate() {
		return serialDate;
	}
	public Item setSerialDate(Date serialDate) {
		checkIfDirty( this.serialDate,serialDate, ItemMetadata.SERIAL_DATE);
		this.serialDate = serialDate;
		return this;
	}
	
	public Date getExpireDate() {
		return expireDate;
	}
	public Item setExpireDate(Date expireDate) {
		checkIfDirty( this.expireDate,expireDate, ItemMetadata.EXPIRE_DATE);
		this.expireDate = expireDate;
		return this;
	}
	
	public String getBarcode() {
		return barcode;
	}
	public Item setBarcode(String barcode) {
		checkIfDirty( this.barcode,barcode, ItemMetadata.BARCODE);
		this.barcode = barcode;
		return this;
	}
	
	public Boolean isActive() {
		return getStatus() == ProductStatus.ACTIVE;
	}
	public ProductStatus getStatus() {
		return status;
	}
	public Item setStatus(ProductStatus status) {
		checkIfDirty( this.status,status, ItemMetadata.STATUS );
		this.status = status;
		return this;
	}

	
	public double getPrice() {
		return price;
	}
	public Item setPrice(double price) {
		checkIfDirty( this.price,price, ItemMetadata.PRICE);
		this.price = price;
		return this;
	}

	public double getExpensesPercent() {
		return expensesPercent;
	}
	public Item setExpensesPercent(double expensesPercent) {
		checkIfDirty( this.expensesPercent,expensesPercent, ItemMetadata.EXPENSES_PERCENT);
		this.expensesPercent = expensesPercent;
		return this;
	}
	
	
	public double getExpensesFixed() {
		return expensesFixed;
	}
	public Item setExpensesFixed(double expensesFixed) {
		checkIfDirty( this.expensesFixed,expensesFixed, ItemMetadata.EXPENSES_FIXED);
		this.expensesFixed = expensesFixed;
		return this;
	}

	public double getProfitPercent() {
		return profitPercent;
	}
	public Item setProfitPercent(double profitPercent) {
		checkIfDirty( this.profitPercent,profitPercent, ItemMetadata.PROFIT_PERCENT);
		this.profitPercent = profitPercent;
		return this;
	}
	
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public Item setPurchasePrice(double purchasePrice) {
		checkIfDirty( this.purchasePrice,purchasePrice, ItemMetadata.PURCHASE_PRICE);
		this.purchasePrice = purchasePrice;
		return this;
	}
	
	public boolean isInternet() {
		return internet;
	}
	public Item setInternet(boolean internet) {
		checkIfDirty( this.internet,internet, ItemMetadata.INTERNET);
		this.internet = internet;
		return this;
	}
	
	public Optional<Tag> getPackFormatTag() {
		return Optional.ofNullable(packFormatTag);
	}
	public Item setPackFormatTag(Tag packFormatTag) {
		checkIfDirty( this.packFormatTag,packFormatTag, ItemMetadata.PACK_FORMAT_TAG);
		this.packFormatTag = packFormatTag;
		return this;
	}
	
	public Integer getPackUnits() {
		return packUnits;
	}
	public Item setPackUnits(Integer packUnits) {
		checkIfDirty( this.packUnits,packUnits, ItemMetadata.PACK_UNITS);
		this.packUnits = packUnits;
		return this;
	}

	public Optional<Tag> getPackUnitsTag() {
		return Optional.ofNullable(packUnitsTag);
	}
	public Item setPackUnitsTag(Tag packUnitsTag) {
		checkIfDirty( this.packUnitsTag,packUnitsTag, ItemMetadata.PACK_UNITS_TAG);
		this.packUnitsTag = packUnitsTag;
		return this;
	}
	
	public double getPackMeasurement() {
		return packMeasurement;
	}
	public Item setPackMeasurement(double packMeasurement) {
		checkIfDirty( this.packMeasurement,packMeasurement, ItemMetadata.PACK_MEASUREMENT);
		this.packMeasurement = packMeasurement;
		return this;
	}

	public Optional<Tag> getPackMeasurementTag() {
		return Optional.ofNullable(packMeasurementTag);
	}
	public Item setPackMeasurementTag(Tag packMeasurementTag) {
		checkIfDirty( this.packMeasurementTag,packMeasurementTag, ItemMetadata.PACK_MEASUREMENT_TAG);
		this.packMeasurementTag = packMeasurementTag;
		return this;
	}

	public Optional<Tag> getStockUnitTag() {
		return Optional.ofNullable(stockUnitTag);
	}
	public Item setStockUnitTag(Tag stockUnitTag) {
		checkIfDirty( this.stockUnitTag,stockUnitTag, ItemMetadata.STOCK_UNIT_TAG);
		this.stockUnitTag = stockUnitTag;
		return this;
	}

	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Item setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Item setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Item setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Item setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getProduct().getName());
		if (AonStringUtils.isNotEmpty(getDetail()) || AonStringUtils.isNotEmpty(getDetail2()) || AonStringUtils.isNotEmpty(getDetail3())) {
			sb.append(' ');
			sb.append('[');
			if (AonStringUtils.isNotEmpty(getDetail())) sb.append(getDetail());
			if (AonStringUtils.isNotEmpty(getDetail2())) {
				if ( sb.length() > 0 ) sb.append("/");
				sb.append(getDetail2());
			}
			if (AonStringUtils.isNotEmpty(getDetail3())) {
				if ( sb.length() > 0 ) sb.append("/");
				sb.append(getDetail3());
			}
			sb.append(']');
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Item) {
			return AonObjectUtils.equals( this.getUuid(),((Item) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
