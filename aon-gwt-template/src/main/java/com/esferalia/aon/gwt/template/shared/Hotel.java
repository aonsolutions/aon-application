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
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomain(Integer domainId) {
		this.domainId = domainId;
	}
	public List<Warehouse> getWarehouses() {
		return warehouses;
	}
	public void setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}

	
	
}
