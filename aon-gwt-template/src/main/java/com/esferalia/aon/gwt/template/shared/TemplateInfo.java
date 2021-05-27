package com.esferalia.aon.gwt.template.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

public class TemplateInfo implements IsSerializable{
	Integer id;
	String name;
	String type;
	LinkedList<String> columns;
	String version;
	Integer domainId;
	String domain;
	Integer mimetype;
	String driveId;
	
	Boolean isParent;
	Boolean hasWarehouse;
	
	public static final ProvidesKey<TemplateInfo> PROVIDES_KEY = new ProvidesKey<TemplateInfo>() {
		@Override
		public Object getKey(TemplateInfo templateInfo) {
			return templateInfo == null ? null : templateInfo.getId();
		}
	};
	
	public String getName() {
		return name;
	}
	public TemplateInfo setName(String name) {
		this.name = name;
		return this;
	}
	public String getType() {
		return type;
	}
	public TemplateInfo setType(String type) {
		this.type = type;
		return this;
	}
	public LinkedList<String> getColumns() {
		return columns;
	}
	public TemplateInfo setColumns(LinkedList<String> columns) {
		this.columns = columns;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public TemplateInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public TemplateInfo setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	public String getDomain() {
		return domain;
	}
	public TemplateInfo setDomain(String domain) {
		this.domain = domain;
		return this;
	}
	public Integer getMimetype() {
		return mimetype;
	}
	public TemplateInfo setMimetype(Integer mimetype) {
		this.mimetype = mimetype;
		return this;
	}
	
	public String getDriveId() {
		return driveId;
	}
	public TemplateInfo setDriveId(String driveId) {
		this.driveId = driveId;
		return this;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public TemplateInfo setIsParent(Boolean isParent) {
		this.isParent = isParent;
		return this;
	}
	public Boolean gethasWarehouse() {
		return hasWarehouse;
	}
	public TemplateInfo sethasWarehouse(Boolean hasWarehouse) {
		this.hasWarehouse = hasWarehouse;
		return this;
	}
	public String getVersion() {
		return version;
	}
	public TemplateInfo setVersion(String version) {
		this.version = version;
		return this;
	}
	
	
	
}
