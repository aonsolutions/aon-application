package com.esferalia.aon.occam.api.model.tariff;

import java.io.Serializable;
import java.util.Date;

public class TariffAddInfo implements Serializable {
	
	private static final long serialVersionUID = 1699629262657144964L;
	
	private Integer id;
	private Integer domain;
	private Tariff tariff;
	private String attribute;
	private String value;
	private Date date;

	public Integer getId() {
		return id;
	}
	public TariffAddInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public TariffAddInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Tariff getTariff() {
		return tariff;
	}
	public TariffAddInfo setTariff(Tariff tariff) {
		this.tariff = tariff;
		return this;
	}
	public String getAttribute() {
		return attribute;
	}
	public TariffAddInfo setAttribute(String attribute) {
		this.attribute = attribute;
		return this;
	}
	public String getValue() {
		return value;
	}
	public TariffAddInfo setValue(String value) {
		this.value = value;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public TariffAddInfo setDate(Date date) {
		this.date = date;
		return this;
	}
	
}
