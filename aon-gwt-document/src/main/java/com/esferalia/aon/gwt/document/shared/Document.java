package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Document implements IsSerializable{
	
	Vector<FileInfo> files = new Vector<FileInfo>();
	Vector<FileInfo> filter = new Vector<FileInfo>();
	Vector<FileInfo> efiles = new Vector<FileInfo>();
	String domain;
	
	public Vector<FileInfo> getFiles() {
		return files;
	}
	public Document setFiles(Vector<FileInfo> files) {
		this.files = files;
		return this;
	}
	public Vector<FileInfo> getFilter() {
		return filter;
	}
	public Document setFilter(Vector<FileInfo> filter) {
		this.filter = filter;
		return this;
	}
	public Vector<FileInfo> getEfiles() {
		return efiles;
	}
	public Document setEfiles(Vector<FileInfo> efiles) {
		this.efiles = efiles;
		return this;
	}
	public String getDomain() {
		return domain;
	}
	public Document setDomain(String domain) {
		this.domain = domain;
		return this;
	}
}
