package com.esferalia.aon.gwt.document.shared;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.gwt.user.client.rpc.IsSerializable;

public class MailAccountList implements IsSerializable{

	private LinkedList<MailAccount> list;
	private ContactList contactList;

	public LinkedList<MailAccount> getList() {
		return list;
	}

	public MailAccountList setList(LinkedList<MailAccount> list) {
		this.list = list;
		return this;
	}

	public ContactList getContactList() {
		return contactList;
	}

	public MailAccountList setContactList(ContactList contactList) {
		this.contactList = contactList;
		return this;
	}
	
	
	
}
