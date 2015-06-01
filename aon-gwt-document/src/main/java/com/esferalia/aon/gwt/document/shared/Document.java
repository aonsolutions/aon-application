package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Document implements IsSerializable{
	
	Vector<FileInfo> files = new Vector<FileInfo>();
	Vector<FileInfo> serviconvenios = new Vector<FileInfo>();
	Vector<FileInfo> filter = new Vector<FileInfo>();
	Vector<FileInfo> efiles = new Vector<FileInfo>();
	String domain;
	Boolean isServiconvenios;
	
	public Boolean getIsServiconvenios() {
		return isServiconvenios;
	}
	public void setIsServiconvenios(Boolean isServiconvenios) {
		this.isServiconvenios = isServiconvenios;
	}
	public Vector<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(Vector<FileInfo> files) {
		this.files = files;
	}
	
	public Vector<FileInfo> getServiconvenios() {
		return serviconvenios;
	}
	public void setServiconvenios(Vector<FileInfo> serviconvenios) {
		this.serviconvenios = serviconvenios;
	}
	public Vector<FileInfo> getFilter() {
		return filter;
	}
	public void setFilter(Vector<FileInfo> filter) {
		this.filter = filter;
	}
	public Vector<FileInfo> getEfiles() {
		return efiles;
	}
	public void setEfiles(Vector<FileInfo> efiles) {
		this.efiles = efiles;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
}
