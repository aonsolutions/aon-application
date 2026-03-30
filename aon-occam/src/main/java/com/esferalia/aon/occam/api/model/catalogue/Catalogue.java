package com.esferalia.aon.occam.api.model.catalogue;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Catalogue implements Serializable {

	private static final long serialVersionUID = 1699629262657144964L;

	private Integer id;
	private Integer domain;
	private String name;
	private boolean purchase;
	private Date start;
	private Date end;
	private List<CatalogueItem> items;
	private List<CatalogueCategory> categories;

	public Integer getId() {
		return id;
	}
	public Catalogue setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Catalogue setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Catalogue setName(String name) {
		this.name = name;
		return this;
	}
	public boolean isPurchase() {
		return purchase;
	}
	public Catalogue setPurchase(boolean purchase) {
		this.purchase = purchase;
		return this;
	}
	public Date getStart() {
		return start;
	}
	public Catalogue setStart(Date start) {
		this.start = start;
		return this;
	}
	public Date getEnd() {
		return end;
	}
	public Catalogue setEnd(Date end) {
		this.end = end;
		return this;
	}

	public List<CatalogueItem> getItems() {
		return items;
	}

	public Catalogue setItems(List<CatalogueItem> items) {
		this.items = items;
		return this;
	}

	public List<CatalogueCategory> getCategories() {
		return categories;
	}

	public Catalogue setCategories(List<CatalogueCategory> categories) {
		this.categories = categories;
		return this;
	}
}
