package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Set;

import com.esferalia.aon.occam.api.model.type.Country;

public class NordigenInstitution implements Serializable {

	private static final long serialVersionUID = 8856305557830632733L;
	
	private String id;//required
	private String name;//required
	private String bic;
	private Integer transactionTotalDays;
	private Set<Country> countries;//required
	private String logo;//required
	
	public String getId() {
		return id;
	}
	public NordigenInstitution setId(String id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public NordigenInstitution setName(String name) {
		this.name = name;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public NordigenInstitution setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public Integer getTransactionTotalDays() {
		return transactionTotalDays;
	}
	public NordigenInstitution setTransactionTotalDays(Integer transactionTotalDays) {
		this.transactionTotalDays = transactionTotalDays;
		return this;
	}
	public Set<Country> getCountries() {
		return countries;
	}
	public NordigenInstitution setCountries(Set<Country> countries) {
		this.countries = countries;
		return this;
	}
	public String getLogo() {
		return logo;
	}
	public NordigenInstitution setLogo(String logo) {
		this.logo = logo;
		return this;
	}
	
	
}
