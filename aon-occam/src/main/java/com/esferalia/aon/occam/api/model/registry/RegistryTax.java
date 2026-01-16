package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Tax;

public class RegistryTax implements Serializable {

	private static final long serialVersionUID = 5630130592270719556L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private Tax tax;
	private double percentage;
	private double surcharge;
	private Date startDate;
	private Date endDate;

	public Integer getId() {
		return this.id;
	}
	public RegistryTax setId(Integer id) {
		this.id = id;
		return this;
	}

	public Tax getTax() {
		return this.tax;
	}
	public RegistryTax setTax(Tax tax) {
		this.tax = tax;
		return this;
	}

	public Integer getRegistry() {
		return this.registry;
	}
	public RegistryTax setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public RegistryTax setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public double getPercentage() {
		return this.percentage;
	}
	public RegistryTax setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getSurcharge() {
		return this.surcharge;
	}
	public RegistryTax setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	
	public double getPercentage( boolean surchargePercent) {
		return surchargePercent ? getSurcharge() : getPercentage();
	}

	public Date getStartDate() {
		return this.startDate;
	}
	public RegistryTax setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return this.endDate;
	}
	public RegistryTax setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

}

