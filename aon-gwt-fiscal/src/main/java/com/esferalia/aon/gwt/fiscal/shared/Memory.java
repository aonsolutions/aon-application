package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class Memory implements Serializable, IsSerializable {

	public static final ProvidesKey<Memory> PROVIDES_KEY = new ProvidesKey<Memory>() {
		@Override
		public Object getKey(Memory memory) {
			return memory == null ? null : memory.getId();
		}
	};

	private Integer id;
	private int domain;
	private String document;
	private String enterpriseName;
	private String description;
	private boolean normalized;
	private int year;
	private Integer depositType;
	
	private byte[] data;

	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isNormalized() {
		return normalized;
	}
	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public Integer getDepositType() {
		return depositType;
	}
	public void setDepositType(Integer depositType) {
		this.depositType = depositType;
	}
	
	
	
	
}
