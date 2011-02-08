package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="bank")
public class Bank implements ITransferObject {
	
	private Integer id;

	private String name;
	
	private String document;
	
	private String address;
	
	private String registralData;
	
	private String web;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false, length=9)
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}
	
	@Column(nullable=false, length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length=128)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="registral_data")
	public String getRegistralData() {
		return registralData;
	}

	public void setRegistralData(String registralData) {
		this.registralData = registralData;
	}

	@Column(length=128)
	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

}