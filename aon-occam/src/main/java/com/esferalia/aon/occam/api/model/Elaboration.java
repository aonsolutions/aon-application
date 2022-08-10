package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	private String description;
	private Warehouse warehouse;
	private double quantity;
	private ElaborationStatus status;
	private String comments;
	private String remarks;
	private ElaborationSource source;
	private Integer sourceId;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private ElaborationDetail detail;
	private List<ElaborationDetail> packaging;
		  
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
	
	public String getReferenceCode() {
		String reference = "";
		if(!AonStringUtils.isBlank(getSeries())) {
			reference = reference + getSeries() + "/";
		}
		reference = reference + AonStringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return reference;
	}
	
	public Date getDate() {
		return date;
	}
	public Elaboration setDate(Date date) {
		this.date = date;
		return this;
	}
	public Item getItem() {
		if(item == null) {
			item = new Item();
		}
		return item;
	}
	public Elaboration setItem(Item item) {
		this.item = item;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Elaboration setDescription(String description) {
		this.description = description;
		return this;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public Elaboration setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public Elaboration setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public ElaborationStatus getStatus() {
		return status;
	}
	
	public String getStatusName() {
		return getStatus() != null ? getStatus().name() : null;
	}
	
	public Byte getStatusValue() {
		return getStatus() != null ? getStatus().value() : null;
	}
	
	public Elaboration setStatus(ElaborationStatus status) {
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
	public String getRemarks() {
		return remarks;
	}
	public Elaboration setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	
	public ElaborationSource getSource() {
		return source;
	}
	
	public String getSourceName() {
		return getSource() != null ? getSource().name() : null;
	}
	
	public Byte getSourceValue() {
		return getSource() != null ? getSource().value() : null;
	}
	
	public Elaboration setSource(ElaborationSource source) {
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
	
	public ElaborationDetail getDetail() {
		return detail;
	}
	
	public Elaboration setDetail(ElaborationDetail detail) {
		this.detail = detail;
		return this;
	}
	
	public List<ElaborationDetail> getPackaging() {
		if(packaging == null) {
			packaging = new LinkedList<>();
		}
		return packaging;
	}
	
	public Elaboration setPackaging(List<ElaborationDetail> packaging) {
		this.packaging = packaging;
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
	
	public boolean isEmpty() {
		return getId() == null && AonStringUtils.isBlank(getSeries()) && getItem().isEmpty();
	}
}
