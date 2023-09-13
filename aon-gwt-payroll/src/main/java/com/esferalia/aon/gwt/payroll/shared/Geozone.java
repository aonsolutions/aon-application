package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Geozone implements Serializable {

	private static final long serialVersionUID = -3272266958736030651L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String code;
	private byte system;
	
	public Geozone() {
		super();
	}
	
	public Integer getId() {
		return id;
	}
	public Geozone setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Geozone setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Geozone setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Geozone setCode(String code) {
		this.code = code;
		return this;
	}
	public byte getSystem() {
		return system;
	}
	public Geozone setSystem(byte system) {
		this.system = system;
		return this;
	}
	
}