package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;

public class RegistrySegment implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Domain domain;
	private Integer registry;
	private Segment segment;

	private boolean removed;
	
	
	public Integer getId() {
		return id;
	}
	public RegistrySegment setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public RegistrySegment setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public RegistrySegment setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Segment getSegment() {
		if(segment==null)
			segment = new Segment();
		return segment;
	}
	
	public RegistrySegment setSegment(Segment segment) {
		this.segment = segment;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistrySegment setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
}
