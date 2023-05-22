package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public class Audit implements Serializable {

	private static final long serialVersionUID = 6995049003419969497L;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Optional<String> getCreationUser() {
		return Optional.ofNullable(creationUser);
	}

	public Audit setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Optional<Date> getCreationDate() {
		return Optional.ofNullable(creationDate);
	}

	public Audit setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public Optional<String> getModificationUser() {
		return Optional.ofNullable(modificationUser);
	}
	public Audit setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Optional<Date> getModificationDate() {
		return Optional.ofNullable(modificationDate);
	}
	public Audit setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
