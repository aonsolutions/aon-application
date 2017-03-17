package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Audit  implements Serializable{
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Audit() {
		
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Audit setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public Audit setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}

	public Audit setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public Audit setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
