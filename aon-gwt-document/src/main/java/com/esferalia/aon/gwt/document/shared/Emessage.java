package com.esferalia.aon.gwt.document.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Emessage implements IsSerializable{
	
	private String recipientsBcc;
	private String recipientsCc;
	private String recipientsTo;
	private String content;
	private String subject;
	private Vector<FileInfo> files;
	public String getRecipientsBcc() {
		return recipientsBcc;
	}
	public void setRecipientsBcc(String recipientsBcc) {
		this.recipientsBcc = recipientsBcc;
	}
	public String getRecipientsCc() {
		return recipientsCc;
	}
	public void setRecipientsCc(String recipientsCc) {
		this.recipientsCc = recipientsCc;
	}
	public String getRecipientsTo() {
		return recipientsTo;
	}
	public void setRecipientsTo(String recipientsTo) {
		this.recipientsTo = recipientsTo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Vector<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(Vector<FileInfo> files) {
		this.files = files;
	}
	
	

}
