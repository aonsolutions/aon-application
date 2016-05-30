package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class Series implements Serializable {

	private static final long serialVersionUID = -579421939986684643L;

	private Integer id;
	private Integer scope;
	private String code;
	private int domain;
	private String description;
	private boolean active;
	private boolean tas;
	private boolean offer;
	private boolean sales;
	private boolean delivery;
	private boolean invoice;
	private boolean rectification;
	private boolean pos;
	private SecurityLevel securityLevel;

	public Integer getId() {
		return id;
	}

	public Series setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public Series setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public String getCode() {
		return code;
	}

	public Series setCode(String code) {
		this.code = code;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Series setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Series setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Series setActive(boolean active) {
		this.active = active;
		return this;
	}

	public boolean isTas() {
		return tas;
	}

	public Series setTas(boolean tas) {
		this.tas = tas;
		return this;
	}

	public boolean isOffer() {
		return offer;
	}

	public Series setOffer(boolean offer) {
		this.offer = offer;
		return this;
	}

	public boolean isSales() {
		return sales;
	}

	public Series setSales(boolean sales) {
		this.sales = sales;
		return this;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public Series setDelivery(boolean delivery) {
		this.delivery = delivery;
		return this;
	}

	public boolean isInvoice() {
		return invoice;
	}

	public Series setInvoice(boolean invoice) {
		this.invoice = invoice;
		return this;
	}

	public boolean isRectification() {
		return rectification;
	}

	public Series setRectification(boolean rectification) {
		this.rectification = rectification;
		return this;
	}

	public boolean isPos() {
		return pos;
	}

	public Series setPos(boolean pos) {
		this.pos = pos;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Series setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Series setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
}
