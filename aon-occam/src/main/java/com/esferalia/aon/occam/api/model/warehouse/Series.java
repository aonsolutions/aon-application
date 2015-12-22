package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Series implements Serializable{

	Byte active;
	String code;
	Byte delivery;
	String description;
	Integer domain;
	Integer id;
	Byte invoice;
	Byte offer;
	Byte pos;
	Byte rectification;
	Byte sales;
	Integer scope;
	Byte securityLevel;
	Byte tas;
	
	public Byte getActive(){
		return active;
	}
	public Series setActive(Byte active) {
		this.active = active;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Series setCode(String code) {
		this.code = code;
		return this;
	}
	public Byte getDelivery() {
		return delivery;
	}
	public Series setDelivery(Byte delivery) {
		this.delivery = delivery;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Series setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Series setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Series setId(Integer id) {
		this.id = id;
		return this;
	}
	public Byte getInvoice() {
		return invoice;
	}
	public Series setInvoice(Byte invoice) {
		this.invoice = invoice;
		return this;
	}
	public Byte getOffer() {
		return offer;
	}
	public Series setOffer(Byte offer) {
		this.offer = offer;
		return this;
	}
	public Byte getPos() {
		return pos;
	}
	public Series setPos(Byte pos) {
		this.pos = pos;
		return this;
	}
	public Byte getRectification() {
		return rectification;
	}
	public Series setRectification(Byte rectification) {
		this.rectification = rectification;
		return this;
	}
	public Byte getSales() {
		return sales;
	}
	public Series setSales(Byte sales) {
		this.sales = sales;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Series setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Byte getSecurityLevel() {
		return securityLevel;
	}
	public Series setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public Byte getTas() {
		return tas;
	}
	public Series setTas(Byte tas) {
		this.tas = tas;
		return this;
	}
}
