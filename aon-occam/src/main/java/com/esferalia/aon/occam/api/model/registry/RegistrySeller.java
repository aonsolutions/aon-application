package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;

public class RegistrySeller implements Serializable {

	private static final long serialVersionUID = -3644754414816978092L;
	
	private Integer id;
	private Domain domain;
	private Integer registry;
	private Seller seller;
	private Date startDate;
	private Date endDate;
	private RegistrySellerStatus status;
	private RegistrySellerType type;
	
	public Integer getId() {
		return id;
	}
	public RegistrySeller setId(Integer id) {
		this.id = id;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public RegistrySeller setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public RegistrySeller setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Seller getSeller() {
		return seller;
	}
	public RegistrySeller setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public RegistrySeller setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public RegistrySeller setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public RegistrySellerStatus getStatus() {
		return status;
	}
	public RegistrySeller setStatus(RegistrySellerStatus status) {
		this.status = status;
		return this;
	}
	public RegistrySellerType getType() {
		return type;
	}
	public RegistrySeller setType(RegistrySellerType type) {
		this.type = type;
		return this;
	}
	
	
}
