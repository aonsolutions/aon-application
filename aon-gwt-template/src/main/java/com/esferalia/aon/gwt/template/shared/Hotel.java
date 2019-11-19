package com.esferalia.aon.gwt.template.shared;

import java.util.List;

import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Hotel implements IsSerializable{
	String name;
	Integer id;
	List<Warehouse> warehouses;
	Integer domainId;
	Integer workplaceId;
	
	
	public String getName() {
		return name;
	}
	public Hotel setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Hotel setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public Hotel setDomain(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	public List<Warehouse> getWarehouses() {
		return warehouses;
	}
	public Hotel setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
		return this;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public Hotel setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}

	
	
}
