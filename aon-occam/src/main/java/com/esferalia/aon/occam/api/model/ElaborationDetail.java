package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class ElaborationDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private int domain;
	private Elaboration elaboration;
	private Date date;
	private OldItem item;
	private double quantity;
	private Warehouse warehouse;
	private String addInfo;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;	
		
	
	public Integer getId() {
		return id;
	}
	public ElaborationDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ElaborationDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Elaboration getElaboration() {
		return elaboration;
	}
	public ElaborationDetail setElaboration(Elaboration elaboration) {
		this.elaboration = elaboration;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public ElaborationDetail setDate(Date date) {
		this.date = date;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public ElaborationDetail setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public ElaborationDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public ElaborationDetail setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public String getAddInfo() {
		return addInfo;
	}
	public ElaborationDetail setAddInfo(String addInfo) {
		this.addInfo = addInfo;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public ElaborationDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public ElaborationDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public ElaborationDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public ElaborationDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
}
