package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.OldItem;

public class WarehouseTransferDetail implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private Date creationDate;
	private String creationUser;
	private Integer domain;
	private Integer id;
	private OldItem item;
	private Date modificationDate;
	private String modificationUser;
	private Double quantity;
	private WarehouseTransfer warehouseTransfer;
	
	public Date getCreationDate() {
		return creationDate;
	}
	public WarehouseTransferDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public WarehouseTransferDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public WarehouseTransferDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public WarehouseTransferDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public WarehouseTransferDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public WarehouseTransferDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public WarehouseTransferDetail setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public WarehouseTransferDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public WarehouseTransfer getWarehouseTransfer() {
		return warehouseTransfer;
	}
	public WarehouseTransferDetail setWarehouseTransfer(WarehouseTransfer warehouseTransfer) {
		this.warehouseTransfer = warehouseTransfer;
		return this;
	}

}
