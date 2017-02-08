package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

public class RecordData implements Serializable{
	
	private static final long serialVersionUID = 9114564405091033572L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
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
	private Integer attach;
	
	public Integer getId() {
		return id;
	}
	public RecordData setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public RecordData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public RecordData setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public RecordData setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public RecordData setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getNotary() {
		return notary;
	}
	public RecordData setNotary(String notary) {
		this.notary = notary;
		return this;
	}
	public String getNumber() {
		return number;
	}
	public RecordData setNumber(String number) {
		this.number = number;
		return this;
	}
	public Date getRecordDate() {
		return recordDate;
	}
	public RecordData setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
		return this;
	}
	public String getVolume() {
		return volume;
	}
	public RecordData setVolume(String volume) {
		this.volume = volume;
		return this;
	}
	public String getSection() {
		return section;
	}
	public RecordData setSection(String section) {
		this.section = section;
		return this;
	}
	public String getPage() {
		return page;
	}
	public RecordData setPage(String page) {
		this.page = page;
		return this;
	}
	public String getSheet() {
		return sheet;
	}
	public RecordData setSheet(String sheet) {
		this.sheet = sheet;
		return this;
	}
	public String getRegistration() {
		return registration;
	}
	public RecordData setRegistration(String registration) {
		this.registration = registration;
		return this;
	}
	public Integer getAttach() {
		return attach;
	}
	public RecordData setAttach(Integer attach) {
		this.attach = attach;
		return this;
	}
}
