package com.code.aon.stat.tas;


public class TasStatHeader {

	private Integer id;
	private String document;
	private String name;
	private Integer tasItem;
	private String publicCode;
	private String make;
	private String model;
	private String tasItemDescription;
	private String tasItemAdditionalInfo;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getTasItem() {
		return tasItem;
	}
	public void setTasItem(Integer tasItem) {
		this.tasItem = tasItem;
	}
	
	public String getPublicCode() {
		return publicCode;
	}
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}
	
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getTasItemDescription() {
		return tasItemDescription;
	}
	public void setTasItemDescription(String tasItemDescription) {
		this.tasItemDescription = tasItemDescription;
	}
	
	public String getTasItemAdditionalInfo() {
		return tasItemAdditionalInfo;
	}
	public void setTasItemAdditionalInfo(String tasItemAdditionalInfo) {
		this.tasItemAdditionalInfo = tasItemAdditionalInfo;
	}
	
	
}
