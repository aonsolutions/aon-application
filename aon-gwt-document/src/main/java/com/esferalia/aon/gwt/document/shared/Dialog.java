package com.esferalia.aon.gwt.document.shared;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;

import gwtupload.client.SingleUploader;


public class Dialog {
	String type;
	String title;
	String nextButtonName;
	Boolean isNextButton;
	String cancelButtonName;
	Boolean isCancelButton;
	String acceptButtonName;
	Boolean isAcceptButton;
	String baseUrl;
	Lists lists;
	SingleUploader upload;
	LinkedList<Domain> sons;
	Boolean son;
	
	FileInfo fileInfo;
	Attach attach;
	
	Tag tag;
	Category cat;
	String searchDomain;
	Boolean multiple;
	Integer num;
	Boolean confidentialUser;
	public Dialog(String type,String title,String cancelButtonName,Boolean isCancelButton,String acceptButtonName,Boolean isAcceptButton,Boolean son){
		this.type = type;
		this.title = title;
		this.cancelButtonName = cancelButtonName;
		this.isCancelButton = isCancelButton;
		this.acceptButtonName = acceptButtonName;
		this.isAcceptButton = isAcceptButton;
		this.son = son;
	}
	
	public String getSearchDomain() {
		return searchDomain;
	}

	public void setSearchDomain(String searchDomain) {
		this.searchDomain = searchDomain;
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

	public LinkedList<Domain> getSons() {
		return sons;
	}

	public void setSons(LinkedList<Domain> sons) {
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

	public String getNextButtonName() {
		return nextButtonName;
	}

	public void setNextButtonName(String nextButtonName) {
		this.nextButtonName = nextButtonName;
	}

	public Boolean getIsNextButton() {
		return isNextButton;
	}

	public void setIsNextButton(Boolean isNextButton) {
		this.isNextButton = isNextButton;
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Boolean getConfidentialUser() {
		return confidentialUser;
	}

	public void setConfidentialUser(Boolean confidentialUser) {
		this.confidentialUser = confidentialUser;
	}

	public Attach getAttach() {
		return attach;
	}

	public void setAttach(Attach attach) {
		this.attach = attach;
	}



	
	
	
}
