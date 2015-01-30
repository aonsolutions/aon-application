package com.esferalia.aon.gwt.template.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

public class TemplateInfo implements IsSerializable{
	Integer id;
	String name;
	String type;
	Vector<String> columns;
	
	Integer domainId;
	String domain;
	Integer mimetype;
	String driveId;
	
	public static final ProvidesKey<TemplateInfo> PROVIDES_KEY = new ProvidesKey<TemplateInfo>() {
		@Override
		public Object getKey(TemplateInfo templateInfo) {
			return templateInfo == null ? null : templateInfo.getId();
		}
	};
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Vector<String> getColumns() {
		return columns;
	}
	public void setColumns(Vector<String> columns) {
		this.columns = columns;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getMimetype() {
		return mimetype;
	}
	public void setMimetype(Integer mimetype) {
		this.mimetype = mimetype;
	}
	
	public String getDriveId() {
		return driveId;
	}
	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}
	
	
	
}
