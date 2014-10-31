package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Document implements IsSerializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1136921611726297915L;
	Vector<FileInfo> files = new Vector<FileInfo>();
	Vector<FileInfo> serviconvenios = new Vector<FileInfo>();
	Vector<String> names = new Vector<String>();
	Vector<String> types = new Vector<String>();
	
	public Vector<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(Vector<FileInfo> files) {
		this.files = files;
	}
	public Vector<String> getNames() {
		return names;
	}
	public void setNames(Vector<String> names) {
		this.names = names;
	}
	
	public Vector<String> getTypes() {
		return types;
	}
	public void setTypes(Vector<String> types) {
		this.types = types;
	}
	public Vector<FileInfo> getServiconvenios() {
		return serviconvenios;
	}
	public void setServiconvenios(Vector<FileInfo> serviconvenios) {
		this.serviconvenios = serviconvenios;
	}
	
	
 
}
