package com.esferalia.aon.occam.api.model.catalogue;

import java.io.Serializable;

public class CatalogueItem implements Serializable {

	private static final long serialVersionUID = -3812045320456789123L;

	private Integer id;
	private Integer domain;
	private Integer catalogue;
	private Integer product;
	private Integer item;
	private Double quantity;
	private Double price;
	private Double discount;

	public Integer getId() {
		return id;
	}

	public CatalogueItem setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public CatalogueItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getCatalogue() {
		return catalogue;
	}

	public CatalogueItem setCatalogue(Integer catalogue) {
		this.catalogue = catalogue;
		return this;
	}

	public Integer getProduct() {
		return product;
	}

	public CatalogueItem setProduct(Integer product) {
		this.product = product;
		return this;
	}

	public Integer getItem() {
		return item;
	}

	public CatalogueItem setItem(Integer item) {
		this.item = item;
		return this;
	}

	public Double getQuantity() {
		return quantity;
	}

	public CatalogueItem setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public CatalogueItem setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getDiscount() {
		return discount;
	}

	public CatalogueItem setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
}
