package com.code.aon.ui.warehouse.controller;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.Tag;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.warehouse.Warehouse;

public class StockExportGwtController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
	Warehouse warehouse;
	String quantity;
	String code;
	String name;
	String barcode;
	String provider;
	Brand brand;
	
	
	private static ProductStatus[] statuses;
	private static String statusesStr;
	
	private static ProductType[] types;
	private static String typesStr;
	
	private static Tag[] tags;
	private static String tagsStr;
	
	private static ProductCategory category;
	
	public String getStatusesStr(){
		return statusesStr;
	}
	public void setStatusesStr(String str){
		StockExportGwtController.statusesStr = str;
	}
	public String getTypesStr(){
		return typesStr;
	}
	public void setTypesStr(String str){
		StockExportGwtController.typesStr = str;
	}
	public String getTagsStr(){
		return tagsStr;
	}
	public void setTagsStr(String str){
		StockExportGwtController.tagsStr = str;
	}

	public ProductStatus[] getStatuses() {
		return statuses;
	}

	public static void setStatuses(ProductStatus[] statuses) {
		StockExportGwtController.statuses = statuses;
		String s ="";
		for(Integer i=0; i<statuses.length; i++){
			s = s+"$"+statuses[i].ordinal();
		}
		StockExportGwtController.statusesStr= s;
	}

	public ProductType[] getTypes() {
		return types;
	}

	public static  void setTypes(ProductType[] types) {
		StockExportGwtController.types = types;
		String s ="";
		for(Integer i=0; i<types.length; i++){
			s = s+"$"+types[i].ordinal();
		}
		StockExportGwtController.typesStr= s;
	}
	
	public Tag[] getTags() {
		return tags;
	}

	public static void setTags(Tag[] tags) {
		StockExportGwtController.tags = tags;
		List<Integer> l = getTagsIds();
		String s ="";
		for(Integer i=0; i<l.size(); i++){
			System.out.println(l.get(i));
			s = s+"$"+l.get(i);
		}
		StockExportGwtController.tagsStr= s;
		
	}

	public int getTagsSize() {
		return ArrayUtils.getLength(tags);
	}
	
	public static List<Integer> getTagsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Tag tag : tags ) {
			if ((tag != null) && (tag.getId() != null)) {
				ids.add(tag.getId());
			}
		}
		return ids;
	}			
	
	public ProductCategory getCategory() {
		return category;
	}

	public static void setCategory(ProductCategory category) {
		StockExportGwtController.category = category;
	}	
	public String getInitialize(){
		
		warehouse = new Warehouse();
		quantity = null;
		code = "";
		name = "";
		barcode = "";
		provider = "";
		brand = new Brand();
	
		return "";
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	
	
}
