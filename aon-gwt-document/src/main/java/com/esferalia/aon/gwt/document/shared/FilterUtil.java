package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FilterUtil implements IsSerializable{

	private Vector<FileInfo> files= new Vector<FileInfo>();
	private String category = new String();
	private String tag = new String();
	
	public FilterUtil() {
	
	}

	public FilterUtil(Vector<FileInfo> v, String c, String t) {
		files = v ;
		category = c;
		tag = t;
	}
		
	public Vector<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(Vector<FileInfo> files) {
		this.files = files;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
	
}
