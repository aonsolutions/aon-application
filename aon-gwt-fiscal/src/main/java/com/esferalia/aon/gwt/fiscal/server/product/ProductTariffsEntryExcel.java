package com.esferalia.aon.gwt.fiscal.server.product;

import java.io.Serializable;
import java.util.TreeMap;

public class ProductTariffsEntryExcel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String code;
	private String description;
	private String type;
	
	private Double price;
	
	private TreeMap<String, Double> tariffs;
	
	public ProductTariffsEntryExcel() {
		super();
	}

	public String getCode() {
		return code;
	}

	public ProductTariffsEntryExcel setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ProductTariffsEntryExcel setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getType() {
		return type;
	}

	public ProductTariffsEntryExcel setType(String type) {
		this.type = type;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}

	public ProductTariffsEntryExcel setPrice(Double price) {
		this.price = price;
		return this;
	}

	public TreeMap<String, Double> getTariffs() {
		return tariffs;
	}

	public ProductTariffsEntryExcel setTariffs(TreeMap<String, Double> tariffs) {
		this.tariffs = tariffs;
		return this;
	}
	
	
	
}
