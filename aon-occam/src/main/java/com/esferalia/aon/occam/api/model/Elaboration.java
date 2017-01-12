package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Item;

public class Elaboration implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private int domain;
	private String series;
	private int number;
	private Date date;
	private Item item;
	private Integer warehouse;
	private double quantity;
	private Byte status;
	private String comments;
	private Byte source;
	private Integer sourceId;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
		  
		
	
	
	public Integer getId() {
		return id;
	}
	public Elaboration setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Elaboration setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public Elaboration setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Elaboration setNumber(int number) {
		this.number = number;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Elaboration setDate(Date date) {
		this.date = date;
		return this;
	}
	public Item getItem() {
		return item;
	}
	public Elaboration setItem(Item item) {
		this.item = item;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public Elaboration setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public Elaboration setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public Elaboration setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public Elaboration setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Byte getSource() {
		return source;
	}
	public Elaboration setSource(Byte source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public Elaboration setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public Elaboration setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Elaboration setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Elaboration setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Elaboration setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
}
