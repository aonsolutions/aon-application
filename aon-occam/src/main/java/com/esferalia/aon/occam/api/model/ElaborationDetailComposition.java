package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class ElaborationDetailComposition implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private int domain;
	private ElaborationDetail elaborationDetail;
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
	public ElaborationDetailComposition setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ElaborationDetailComposition setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public ElaborationDetail getElaborationDetail() {
		return elaborationDetail;
	}
	public ElaborationDetailComposition setElaborationDetail(ElaborationDetail elaborationDetail) {
		this.elaborationDetail = elaborationDetail;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public ElaborationDetailComposition setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public ElaborationDetailComposition setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public ElaborationDetailComposition setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public String getAddInfo() {
		return addInfo;
	}
	public ElaborationDetailComposition setAddInfo(String addInfo) {
		this.addInfo = addInfo;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public ElaborationDetailComposition setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public ElaborationDetailComposition setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public ElaborationDetailComposition setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public ElaborationDetailComposition setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
}
