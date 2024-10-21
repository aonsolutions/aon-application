package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class Geozone implements Serializable {

	private static final long serialVersionUID = -1293760694660980945L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String code;
	private boolean system;

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

	public String getCode() {
		return code;
	}
	public Geozone setCode(String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}
	public Geozone setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSystem() {
		return system;
	}
	public Geozone setSystem(boolean system) {
		this.system = system;
		return this;
	}
	
}
