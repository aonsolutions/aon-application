package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Date;

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
	private boolean pinpUp = false;
	private String noteTag;
	private String color;
	private Date archiveDate;
	private Date modificationDate;
	private Date creationDate;
	
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
	public boolean isPinpUp() {
		return pinpUp;
	}
	public Note setPinpUp(boolean pinpUp) {
		this.pinpUp = pinpUp;
		return this;
	}
	public String getNoteTag() {
		return noteTag;
	}
	public Note setNoteTag(String noteTag) {
		this.noteTag = noteTag;
		return this;
	}
	public String getColor() {
		return color;
	}
	public Note setColor(String color) {
		this.color = color;
		return this;
	}
	public Date getArchiveDate() {
		return archiveDate;
	}
	public Note setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Note setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Note setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
}
