package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ContactList implements IsSerializable{

	Vector<Contact> list = new Vector<Contact>();

	public ContactList() {

	}
	
	public Vector<Contact> getList() {
		return list;
	}

	public void setList(Vector<Contact> list) {
		this.list = list;
	}
	
}
