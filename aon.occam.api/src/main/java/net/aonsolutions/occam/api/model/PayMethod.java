package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.type.PayMethodType;

public class PayMethod implements Serializable {

	private static final long serialVersionUID = -7471690013821688320L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private PayMethodType type;

	public Integer getId() {
		return id;
	}
	public PayMethod setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public PayMethod setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getName() {
		return name;
	}
	public PayMethod setName(String name) {
		this.name = name;
		return this;
	}
	
	public PayMethodType getType() {
		return type;
	}
	public PayMethod setType(PayMethodType type) {
		this.type = type;
		return this;
	}
	
}
