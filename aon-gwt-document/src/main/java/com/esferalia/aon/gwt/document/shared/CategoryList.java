package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CategoryList implements IsSerializable{

	Vector<Category> list = new Vector<Category>();

	public CategoryList() {

	}
	
	public Vector<Category> getList() {
		return list;
	}

	public void setList(Vector<Category> list) {
		this.list = list;
	}
	
}
