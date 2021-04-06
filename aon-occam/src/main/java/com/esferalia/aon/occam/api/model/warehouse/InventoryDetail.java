package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;

public class InventoryDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private int domain;
	private Inventory inventory;
	
	private Double actualQuantity;
	private Double cost;
	private OldItem item;
	private Double realQuantity;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	public InventoryDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public InventoryDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public InventoryDetail setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public InventoryDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public InventoryDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public InventoryDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public InventoryDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Inventory getInventory() {
		return inventory;
	}
	public InventoryDetail setInventory(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}
	public Double getActualQuantity() {
		return actualQuantity;
	}
	public InventoryDetail setActualQuantity(Double actualQuantity) {
		this.actualQuantity = actualQuantity;
		return this;
	}
	public Double getCost() {
		return cost;
	}
	public InventoryDetail setCost(Double cost) {
		this.cost = cost;
		return this;
	}
	public Double getRealQuantity() {
		return realQuantity;
	}
	public InventoryDetail setRealQuantity(Double realQuantity) {
		this.realQuantity = realQuantity;
		return this;
	}
	

}
