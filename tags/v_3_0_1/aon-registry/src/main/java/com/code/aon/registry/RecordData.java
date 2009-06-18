package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="record_data")
public class RecordData implements ITransferObject {
	
	private Integer id;
	
	private Registry registry;
	
	private Date creationDate;
	
	private String description;
	
	private String notary;
	
	private String number;
	
	private Date recordDate;
	
	private String volume;
	
	private String section;
	
	private String page;
	
	private String sheet;
	
	private String registration;
	
	private RegistryAttachment attach; 

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="registry", nullable=false)
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(name="creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(length=64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=64)
	public String getNotary() {
		return notary;
	}

	public void setNotary(String notary) {
		this.notary = notary;
	}

	@Column(length=16)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name="record_date")
	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	@Column(length=16)
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Column(length=16)
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	@Column(length=16)
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	@Column(length=16)
	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	@Column(length=64)
	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	@ManyToOne
	@JoinColumn(name="attach")
	public RegistryAttachment getAttach() {
		return attach;
	}

	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}
}