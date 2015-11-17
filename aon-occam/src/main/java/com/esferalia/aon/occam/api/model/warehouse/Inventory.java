package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

public class Inventory implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private Integer id;
	private int domain;
	
	private Date inventoryDate;
	private Integer warehouse;
	private String description;
	private Integer status;

	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	public Inventory setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Inventory setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Inventory setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Inventory setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Inventory setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Inventory setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Date getInventoryDate() {
		return inventoryDate;
	}
	public Inventory setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public Inventory setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Inventory setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getStatus() {
		return status;
	}
	public Inventory setStatus(Integer status) {
		this.status = status;
		return this;
	}
	
}
