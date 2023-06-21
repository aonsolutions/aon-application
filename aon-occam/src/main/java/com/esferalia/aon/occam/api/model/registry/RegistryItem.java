package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.Priority;

public class RegistryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private Item item;
	private RegistryMode type;
	private String code;
	private Double price;
	private String discountExpr;
	private Priority priority;
	private Integer workplace;
	private RegistryItemStatus status;
	private String quantity;
	private Date startDate;
	private Date endDate;
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	private boolean removed;

	public Integer getId() {
		return id;
	}

	public RegistryItem setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryItem setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Item getItem() {
		return item;
	}

	public RegistryItem setItem(Item item) {
		this.item = item;
		return this;
	}

	public RegistryMode getType() {
		return type;
	}

	public RegistryItem setType(RegistryMode type) {
		this.type = type;
		return this;
	}

	public String getCode() {
		return code;
	}

	public RegistryItem setCode(String code) {
		this.code = code;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public RegistryItem setPrice(Double price) {
		this.price = price;
		return this;
	}

	public String getDiscountExpr() {
		return discountExpr;
	}

	public RegistryItem setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}

	public Priority getPriority() {
		return priority;
	}

	public RegistryItem setPriority(Priority priority) {
		this.priority = priority;
		return this;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public RegistryItem setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public RegistryItemStatus getStatus() {
		return status;
	}

	public RegistryItem setStatus(RegistryItemStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistryItem setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}

	public String getQuantity() {
		return quantity;
	}

	public RegistryItem setQuantity(String quantity) {
		this.quantity = quantity;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public RegistryItem setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public RegistryItem setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public RegistryItem setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public RegistryItem setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public RegistryItem setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public RegistryItem setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public RegistryItem setBookingStatus(BookingStatus bookingStatus) {
		if (bookingStatus != null && RegistryMode.BOOKING.equals(type)) {
			this.status = RegistryItemStatus.safeValueOf(bookingStatus.value());
		}
		return this;
	}
	
	public BookingStatus getBookingStatus() {
		if (RegistryMode.BOOKING.equals(this.type) && this.status != null) {
			return BookingStatus.safeValueOf(status.value());
		}
		return null;
	}
	
}
