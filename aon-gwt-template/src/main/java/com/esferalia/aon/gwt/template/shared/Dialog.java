package com.esferalia.aon.gwt.template.shared;

public class Dialog {
	
	String title;
	String accept;
	Boolean boolAccept;
	String cancel;
	Boolean boolCancel;
	String type;
	TemplateInfo templateInfo;
	String url;
	TemplateList templateList;
	Error error;
	public Dialog() {
	
	}
	
	public Dialog(	String title, String accept,
				Boolean boolAccept, String cancel,
				Boolean boolCancel, String type) {
		this.title = title;
		this.accept = accept;
		this.boolAccept = boolAccept;
		this.cancel = cancel;
		this.boolCancel = boolCancel;
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public Boolean getBoolAccept() {
		return boolAccept;
	}
	public void setBoolAccept(Boolean boolAccept) {
		this.boolAccept = boolAccept;
	}
	public String getCancel() {
		return cancel;
	}
	public void setCancel(String cancel) {
		this.cancel = cancel;
	}
	public Boolean getBoolCancel() {
		return boolCancel;
	}
	public void setBoolCancel(Boolean boolCancel) {
		this.boolCancel = boolCancel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TemplateList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(TemplateList templateList) {
		this.templateList = templateList;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
	
	

}
