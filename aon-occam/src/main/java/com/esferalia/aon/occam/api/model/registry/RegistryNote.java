package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class RegistryNote implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String description;
	private Date noteDate;
	private String comments;
	private NoteType noteType;
	private SecurityLevel securityLevel;
	
	public Integer getId() {
		return id;
	}

	public RegistryNote setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryNote setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryNote setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public RegistryNote setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public RegistryNote setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public RegistryNote setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public NoteType getNoteType() {
		return noteType;
	}

	public RegistryNote setNoteType(NoteType noteType) {
		this.noteType = noteType;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public RegistryNote setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL.equals(getSecurityLevel());
	}
	
	public RegistryNote setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
}
