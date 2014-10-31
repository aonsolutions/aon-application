package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TagList implements IsSerializable {

	Vector<Tag> list = new Vector<Tag>();

	public TagList() {
		
	}
	
	public Vector<Tag> getList() {
		return list;
	}

	public void setList(Vector<Tag> list) {
		this.list = list;
	}

	public Integer getLength() {
		return list.size();
	}

}
