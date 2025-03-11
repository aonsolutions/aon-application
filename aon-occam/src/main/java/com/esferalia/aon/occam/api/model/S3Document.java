package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.MimeType;

public class S3Document implements Serializable {
	
	private static final long serialVersionUID = 1405756456946179578L;

	public S3Document() {}
	
	private Integer id;
	private Integer type;
	private Integer domain;
	private Integer registry;
	private MimeType mimetype;

	private String name;
	private Integer scope;
	private Byte securityLevel;
	private Date documentDate;
	private String s3key;
	private String s3bucket;
	private Integer category;
	
	private String creationUser;
	private String modificationUser;
	private Date creationDate;
	private Date modificationDate;

	public Integer getId() {
		return id;
	}

	public S3Document setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public S3Document setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public S3Document setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public MimeType getMimetype() {
		return mimetype;
	}

	public S3Document setMimetype(MimeType mimetype) {
		this.mimetype = mimetype;
		return this;
	}

	public String getName() {
		return name;
	}

	public S3Document setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public S3Document setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public Integer getCategory() {
		return category;
	}

	public S3Document setCategory(Integer category) {
		this.category = category;
		return this;
	}

	public Byte getSecurityLevel() {
		return securityLevel;
	}

	public S3Document setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public Date getDocumentDate() {
		return documentDate;
	}

	public S3Document setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
		return this;
	}

	public String getS3key() {
		return s3key;
	}

	public S3Document setS3key(String s3key) {
		this.s3key = s3key;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public S3Document setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public S3Document setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public S3Document setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public S3Document setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String getS3bucket() {
		return s3bucket;
	}

	public S3Document setS3bucket(String s3bucket) {
		this.s3bucket = s3bucket;
		return this;
	}
	
	public Integer getType() {
		return type;
	}

	public S3Document setType(Integer type) {
		this.type = type;
		return this;
	}
	
}
