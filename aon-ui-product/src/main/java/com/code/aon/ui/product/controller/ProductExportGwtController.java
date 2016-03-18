package com.code.aon.ui.product.controller;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.controller.AuditableSearchController;

public class ProductExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- PRODUCT FILTER

	private String code;
	private String name;
	private static ProductStatus[] statuses;
	private static String statusesStr;
	private static ProductType[] types;
	private static String typesStr;
	private static ProductKind[] kinds;
	private static String kindsStr;
	private static ProductCategory category;
	private static Tax vat;
	private static Tax retention;
	private static Account purchaseAccount;
	private static Account salesAccount;
	private static Tag[] tags;
	private static String tagsStr;
	private static Supplier supplier;
	private static String supplierCode;

	private Boolean inventoriable;
	private Boolean serializable;
	private Boolean manufactured;
	private Boolean composition;
	private Boolean packaged;
	private Brand brand;

	//-------------------- ITEM FILTER

	private String itemSerialDate1;
	private String itemSerialDate2;
	private String barcode;
	private String serialNumber;
	private String detail;
	private String detail2;
	private String detail3; 
	private String description;
	private Double purchasePrice;
	private Double profitPercent;
	private Double price;
	private Integer internet;
	private static ProductStatus[] itemStatuses;
	private static String itemStatusesStr;

	public String getInitialize(){
		code = "";
		name = "";
		types = null;
		kinds = null;
		category = new ProductCategory();
		vat = new Tax();
		retention = new Tax();
		purchaseAccount = new Account(); 
		salesAccount = new Account();

		inventoriable = null;
		serializable = null;
		manufactured = null;
		composition = null;
		packaged = null;
		brand = new Brand();

		itemSerialDate1 ="";
		itemSerialDate2 ="";
		barcode = "";
		serialNumber = "";
		detail = "";
		detail2 = "";
		detail3 = ""; 
		description = "";
		purchasePrice = null;
		profitPercent = null;
		price = null;
		internet = null;

		setCreationUser("");
		setCreationDate1(null);
		setCreationDate2(null);
		setModificationUser("");
		setModificationDate1(null);
		setModificationDate2(null);

		return "";
	}

	public ProductExportGwtController() {
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ProductStatus[] getStatuses() {
		return statuses;
	}
	public static void setStatuses(ProductStatus[] statuses) {
		ProductExportGwtController.statuses = statuses;
		String s = "";
		for (Integer i=0; i<statuses.length; i++){
			s = s+"$"+statuses[i].ordinal();
		}
		ProductExportGwtController.statusesStr= s;
	}

	public String getStatusesStr() {
		return statusesStr;
	}
	public void setStatusesStr(String statusesStr) {
		ProductExportGwtController.statusesStr = statusesStr;
	}

	public ProductType[] getTypes() {
		return types;
	}
	public static void setTypes(ProductType[] types) {
		ProductExportGwtController.types = types;
		String s ="";
		for(Integer i=0; i<types.length; i++) {
			s = s+"$"+types[i].ordinal();
		}
		ProductExportGwtController.typesStr = s;
	}

	public String getTypesStr() {
		return typesStr;
	}
	public void setTypesStr(String typesStr) {
		ProductExportGwtController.typesStr = typesStr;
	}

	public ProductKind[] getKinds() {
		return kinds;
	}
	public static void setKinds(ProductKind[] kinds) {
		ProductExportGwtController.kinds = kinds;
		String s ="";
		for(Integer i=0; i<kinds.length; i++) {
			s = s+"$"+kinds[i].ordinal();
		}
		ProductExportGwtController.kindsStr = s;
	}

	public String getKindsStr() {
		return kindsStr;
	}
	public void setKindsStr(String kindsStr) {
		ProductExportGwtController.kindsStr = kindsStr;
	}

	public ProductCategory getCategory() {
		return category;
	}
	public static void setCategory(ProductCategory category) {
		ProductExportGwtController.category = category;
	}

	public Tax getVat() {
		return vat;
	}
	public static void  setVat(Tax vat) {
		ProductExportGwtController.vat = vat;
	}

	public Tax getRetention() {
		return retention;
	}
	public static void setRetention(Tax retention) {
		ProductExportGwtController.retention = retention;
	}

	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public static void setPurchaseAccount(Account purchaseAccount) {
		ProductExportGwtController.purchaseAccount = purchaseAccount;
	}

	public Account getSalesAccount() {
		return salesAccount;
	}
	public  static void setSalesAccount(Account salesAccount) {
		ProductExportGwtController.salesAccount = salesAccount;
	}

	public Tag[] getTags() {
		return tags;
	}
	public static void setTags(Tag[] tags) {
		ProductExportGwtController.tags = tags;
		List<Integer> l = getTagsIds();
		String s ="";
		for (Integer i=0; i<l.size(); i++) {
			System.out.println(l.get(i));
			s = s+"$"+l.get(i);
		}
		ProductExportGwtController.tagsStr = s;
	}
	public static List<Integer> getTagsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		if (tags != null) {
			for(Tag tag : tags) {
				if ((tag != null) && (tag.getId() != null)) {
					ids.add(tag.getId());
				}
			}
		}
		return ids;
	}			

	public String getTagsStr() {
		return tagsStr;
	}
	public void setTagsStr(String tagsStr) {
		ProductExportGwtController.tagsStr = tagsStr;
	}

	public Supplier getSupplier() {
		return supplier;
	}
	public static void setSupplier(Supplier supplier) {
		ProductExportGwtController.supplier = supplier;
	}

	public String getSupplierCode() {
		return supplierCode;
	}
	public static void setSupplierCode(String supplierCode) {
		ProductExportGwtController.supplierCode = supplierCode;
	}

	public Boolean getInventoriable() {
		return inventoriable;
	}
	public void setInventoriable(Boolean inventoriable) {
		this.inventoriable = inventoriable;
	}

	public Boolean getSerializable() {
		return serializable;
	}
	public void setSerializable(Boolean serializable) {
		this.serializable = serializable;
	}

	public Boolean getManufactured() {
		return manufactured;
	}
	public void setManufactured(Boolean manufactured) {
		this.manufactured = manufactured;
	}

	public Boolean getComposition() {
		return composition;
	}
	public void setComposition(Boolean composition) {
		this.composition = composition;
	}

	public Boolean getPackaged() {
		return packaged;
	}
	public void setPackaged(Boolean packaged) {
		this.packaged = packaged;
	}

	public Brand getBrand() {
		return brand;
	}
	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public String getItemSerialDate1() {
		return itemSerialDate1;
	}
	public void setItemSerialDate1(String itemSerialDate1) {
		this.itemSerialDate1 = itemSerialDate1;
	}

	public String getItemSerialDate2() {
		return itemSerialDate2;
	}
	public void setItemSerialDate2(String itemSerialDate2) {
		this.itemSerialDate2 = itemSerialDate2;
	}

	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getProfitPercent() {
		return profitPercent;
	}
	public void setProfitPercent(Double profitPercent) {
		this.profitPercent = profitPercent;
	}

	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getInternet() {
		return internet;
	}
	public void setInternet(Integer internet) {
		this.internet = internet;
	}

	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}
	public static void setItemStatuses(ProductStatus[] itemStatuses) {
		ProductExportGwtController.itemStatuses = itemStatuses;
		String s = "";
		for (Integer i=0; i<itemStatuses.length; i++) {
			s = s+"$"+itemStatuses[i].ordinal();
		}
		ProductExportGwtController.itemStatusesStr = s;
	}

	public String getItemStatusesStr() {
		return itemStatusesStr;
	}
	public void setItemStatusesStr(String itemStatusesStr) {
		ProductExportGwtController.itemStatusesStr = itemStatusesStr;
		
	}

}