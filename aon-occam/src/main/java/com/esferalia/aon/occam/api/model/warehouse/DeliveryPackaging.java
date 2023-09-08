package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Item;

public class DeliveryPackaging implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3053139577316342602L;
	private Integer id;
	private Integer domain;
	private Delivery delivery;
	private Item item;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	
	public DeliveryPackaging setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public DeliveryPackaging setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Delivery getDelivery() {
		if(delivery == null) 
			delivery = new Delivery();
		return delivery;
	}
	public DeliveryPackaging setDelivery(Delivery delivery) {
		this.delivery = delivery;
		return this;
	}

	public Item getItem() {
		if(item == null) 
			item = new Item();
		return item;
	}
	public DeliveryPackaging setItem(Item item) {
		this.item = item;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	public DeliveryPackaging setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public DeliveryPackaging setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public DeliveryPackaging setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public DeliveryPackaging setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public boolean isEmpty() {
		return getItem().isEmpty()
			&& getDelivery().getId() == null;
	}

}
