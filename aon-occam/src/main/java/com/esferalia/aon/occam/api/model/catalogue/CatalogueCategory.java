package com.esferalia.aon.occam.api.model.catalogue;

import java.io.Serializable;

public class CatalogueCategory implements Serializable {

	private static final long serialVersionUID = 5921034856712398745L;

	private Integer id;
	private Integer domain;
	private Integer catalogue;
	private Integer category;
	private Double quantity;
	private Double discount;

	public Integer getId() {
		return id;
	}

	public CatalogueCategory setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public CatalogueCategory setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getCatalogue() {
		return catalogue;
	}

	public CatalogueCategory setCatalogue(Integer catalogue) {
		this.catalogue = catalogue;
		return this;
	}

	public Integer getCategory() {
		return category;
	}

	public CatalogueCategory setCategory(Integer category) {
		this.category = category;
		return this;
	}

	public Double getQuantity() {
		return quantity;
	}

	public CatalogueCategory setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}

	public Double getDiscount() {
		return discount;
	}

	public CatalogueCategory setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
}
