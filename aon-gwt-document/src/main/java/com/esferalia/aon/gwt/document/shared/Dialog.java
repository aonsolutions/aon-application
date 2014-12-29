package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import gwtupload.client.SingleUploader;


public class Dialog {
	String type;
	String title;
	String cancelButtonName;
	Boolean isCancelButton;
	String acceptButtonName;
	Boolean isAcceptButton;
	String baseUrl;
	Lists lists;
	SingleUploader upload;
	Vector<Domain> sons;
	Boolean son;
	FileInfo fileInfo;
	Tag tag;
	Category cat;
 	
	public Dialog(String type,String title,String cancelButtonName,Boolean isCancelButton,String acceptButtonName,Boolean isAcceptButton,Boolean son){
		this.type = type;
		this.title = title;
		this.cancelButtonName = cancelButtonName;
		this.isCancelButton = isCancelButton;
		this.acceptButtonName = acceptButtonName;
		this.isAcceptButton = isAcceptButton;
		this.son = son;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCancelButtonName() {
		return cancelButtonName;
	}

	public void setCancelButtonName(String cancelButtonName) {
		this.cancelButtonName = cancelButtonName;
	}

	public Boolean getIsCancelButton() {
		return isCancelButton;
	}

	public void setIsCancelButton(Boolean isCancelButton) {
		this.isCancelButton = isCancelButton;
	}

	public String getAcceptButtonName() {
		return acceptButtonName;
	}

	public void setAcceptButtonName(String acceptButtonName) {
		this.acceptButtonName = acceptButtonName;
	}

	public Boolean getIsAcceptButton() {
		return isAcceptButton;
	}

	public void setIsAcceptButton(Boolean isAcceptButton) {
		this.isAcceptButton = isAcceptButton;
	}

	public Lists getLists() {
		return lists;
	}

	public void setLists(Lists lists) {
		this.lists = lists;
	}

	public SingleUploader getUpload() {
		return upload;
	}

	public void setUpload(SingleUploader upload) {
		this.upload = upload;
	}

	public Vector<Domain> getSons() {
		return sons;
	}

	public void setSons(Vector<Domain> sons) {
		this.sons = sons;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public Boolean getSon() {
		return son;
	}

	public void setSon(Boolean son) {
		this.son = son;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Category getCat() {
		return cat;
	}

	public void setCat(Category cat) {
		this.cat = cat;
	}



	
	
	
}
