package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Priority;

public class RegistryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private Integer item;
	private RegistryMode type;
	private String code;
	private Double price;
	private String discountExpr;
	private Priority priority;
	private Integer workplace;
	private RegistryItemStatus status;
	
	private boolean removed;

	public Integer getId() {
		return id;
	}

	public RegistryItem setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryItem setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getItem() {
		return item;
	}

	public RegistryItem setItem(Integer item) {
		this.item = item;
		return this;
	}

	public RegistryMode getType() {
		return type;
	}

	public RegistryItem setType(RegistryMode type) {
		this.type = type;
		return this;
	}

	public String getCode() {
		return code;
	}

	public RegistryItem setCode(String code) {
		this.code = code;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public RegistryItem setPrice(Double price) {
		this.price = price;
		return this;
	}

	public String getDiscountExpr() {
		return discountExpr;
	}

	public RegistryItem setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}

	public Priority getPriority() {
		return priority;
	}

	public RegistryItem setPriority(Priority priority) {
		this.priority = priority;
		return this;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public RegistryItem setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public RegistryItemStatus getStatus() {
		return status;
	}

	public RegistryItem setStatus(RegistryItemStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistryItem setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
}
