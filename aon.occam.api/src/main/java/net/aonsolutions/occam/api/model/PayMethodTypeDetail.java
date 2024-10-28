package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.type.PayMethodType;

public class PayMethodTypeDetail implements Serializable {
	private static final long serialVersionUID = -31089307097383442L;
	
	private Integer id;
	private Integer domain;
	private PayMethodType type;
	private String description;
	private Account account;

	public Integer getId() {
		return id;
	}
	public PayMethodTypeDetail setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public PayMethodTypeDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public PayMethodTypeDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public PayMethodType getType() {
		return type;
	}
	public PayMethodTypeDetail setType(PayMethodType type) {
		this.type = type;
		return this;
	}
	
	public Account getAccount() {
		return account;
	}
	public PayMethodTypeDetail setAccount(Account account) {
		this.account = account;
		return this;
	}
}
