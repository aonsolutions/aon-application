package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MailAccountList implements IsSerializable{

	private Vector<MailAccount> list;
	private ContactList contactList;

	public Vector<MailAccount> getList() {
		return list;
	}

	public void setList(Vector<MailAccount> list) {
		this.list = list;
	}

	public ContactList getContactList() {
		return contactList;
	}

	public void setContactList(ContactList contactList) {
		this.contactList = contactList;
	}
	
	
	
}
