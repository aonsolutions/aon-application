package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class DomainLinked implements Serializable {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer registry;
	private Integer id;
	private String name;
	private String schema;
	private String type;
	private Integer index;
	
	public Integer getRegistry() {
		return registry;
	}
	
	public DomainLinked setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Integer getId() {
		return id;
	}

	public DomainLinked setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public DomainLinked setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public DomainLinked setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public String getType() {
		return type;
	}
	
	public DomainLinked setType(String type) {
		this.type = type;
		return this;
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public DomainLinked setIndex(Integer index) {
		this.index = index;
		return this;
	}
}
