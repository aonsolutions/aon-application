package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class RegistryPayMethod implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private Integer payMethod;
	private Integer rbank;
	private Short numberOfPymnts;
	private Short daysToFirstPymnt;
	private Short daysBetwenPymnts;
	private String pymnt_days;
	
	public RegistryPayMethod() {
	
	}
	
	public Integer getId() {
		return id;
	}
	public RegistryPayMethod setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public RegistryPayMethod setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public RegistryPayMethod setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public RegistryPayMethod setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public Integer getRbank() {
		return rbank;
	}
	public RegistryPayMethod setRbank(Integer rbank) {
		this.rbank = rbank;
		return this;
	}
	public Short getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public RegistryPayMethod setNumberOfPymnts(Short numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public Short getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public RegistryPayMethod setDaysToFirstPymnt(Short daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public Short getDaysBetwenPymnts() {
		return daysBetwenPymnts;
	}
	public RegistryPayMethod setDaysBetwenPymnts(Short daysBetwenPymnts) {
		this.daysBetwenPymnts = daysBetwenPymnts;
		return this;
	}
	public String getPymnt_days() {
		return pymnt_days;
	}
	public RegistryPayMethod setPymnt_days(String pymnt_days) {
		this.pymnt_days = pymnt_days;
		return this;
	}
}
