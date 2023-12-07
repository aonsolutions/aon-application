package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.office.Tag;

public class Note implements Serializable {
	
	private static final long serialVersionUID = -6673378118872331822L;
	
	public Note() {}
	
	private Integer id;
	private Integer domain;
	private String subject;
	private Date date;
	private Integer owner;
	private String note;
	private boolean archive = false;
	private Tag tag;
	
	public Integer getId() {
		return id;
	}
	public Note setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Note setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getSubject() {
		return subject;
	}
	public Note setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Note setDate(Date date) {
		this.date = date;
		return this;
	}
	public Integer getOwner() {
		return owner;
	}
	public Note setOwner(Integer owner) {
		this.owner = owner;
		return this;
	}
	public String getNote() {
		return note;
	}
	public Note setNote(String note) {
		this.note = note;
		return this;
	}
	
	public boolean isArchive() {
		return archive;
	}
	public Note setArchive(boolean archive) {
		this.archive = archive;
		return this;
	}
	public Tag getTag() {
		return tag;
	}
	public Note setTag(Tag tag) {
		this.tag = tag;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
