package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class Tariff implements Serializable {
	
	private static final long serialVersionUID = 1699629262657144964L;
	
	private Integer id;
	private Integer domain;
	private String code;
	private String name;
	private boolean purchase;
	private double discount;
	private boolean active;

	public Integer getId() {
		return id;
	}
	public Tariff setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Tariff setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	public Tariff setCode(String code) {
		this.code = code;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Tariff setName(String name) {
		this.name = name;
		return this;
	}
	
	public boolean isPurchase() {
		return purchase;
	}
	public Tariff setPurchase(boolean purchase) {
		this.purchase = purchase;
		return this;
	}
	
	public double getDiscount() {
		return discount;
	}
	public Tariff setDiscount(double discount) {
		this.discount = discount;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public Tariff setActive(boolean active) {
		this.active = active;
		return this;
	}

}
