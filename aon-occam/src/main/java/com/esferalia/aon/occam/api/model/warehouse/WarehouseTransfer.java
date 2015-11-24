package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

public class WarehouseTransfer implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private String comments;
	private Date creationDate;
	private String creationUser;
	private Integer domain;
	private Integer id;
	private Inventory inventory;
	private Date issueTime;
	private Date modificationDate;
	private String modificationUser;
	private Integer number;
	private String series;
	private Byte source;
	private Integer sourceId;
	private Integer sourceWarehouse;
	private Integer targetWarehouse;
	
	public String getComments() {
		return comments;
	}
	public WarehouseTransfer setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public WarehouseTransfer setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public WarehouseTransfer setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public WarehouseTransfer setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public WarehouseTransfer setId(Integer id) {
		this.id = id;
		return this;
	}
	public Inventory getInventory() {
		return inventory;
	}
	public WarehouseTransfer setInventory(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}
	public Date getIssueTime() {
		return issueTime;
	}
	public WarehouseTransfer setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public WarehouseTransfer setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public WarehouseTransfer setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public WarehouseTransfer setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public WarehouseTransfer setSeries(String series) {
		this.series = series;
		return this;
	}
	public Byte getSource() {
		return source;
	}
	public WarehouseTransfer setSource(Byte source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public WarehouseTransfer setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	public Integer getSourceWarehouse() {
		return sourceWarehouse;
	}
	public WarehouseTransfer setSourceWarehouse(Integer sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
		return this;
	}
	public Integer getTargetWarehouse() {
		return targetWarehouse;
	}
	public WarehouseTransfer setTargetWarehouse(Integer targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
		return this;
	}
}
