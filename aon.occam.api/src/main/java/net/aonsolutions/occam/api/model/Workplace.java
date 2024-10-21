package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class Workplace implements Serializable {
	
	private static final long serialVersionUID = -6673378118872331822L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private boolean active;
	private Integer address;
	private Integer customer;
	private Byte economicagreement;
	private Integer enterprise;
	private Integer scope;
	
	public boolean isActive() {
		return active;
	}
	public Workplace setActive(boolean active) {
		this.active = active;
		return this;
	}
	public Integer getAddress() {
		return address;
	}
	public Workplace setAddress(Integer address) {
		this.address = address;
		return this;
	}
	public Integer getCustomer() {
		return customer;
	}
	public Workplace setCustomer(Integer customer) {
		this.customer = customer;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Workplace setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Workplace setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Byte getEconomicagreement() {
		return economicagreement;
	}
	public Workplace setEconomicagreement(Byte economicagreement) {
		this.economicagreement = economicagreement;
		return this;
	}
	public Integer getEnterprise() {
		return enterprise;
	}
	public Workplace setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Workplace setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Workplace setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
}
