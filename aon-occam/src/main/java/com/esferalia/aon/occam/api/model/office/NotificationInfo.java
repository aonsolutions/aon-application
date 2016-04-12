package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;

@SuppressWarnings("serial")
public class NotificationInfo implements Serializable{
	private LinkedList<MailAccount> mailAccountList;
	private LinkedList<Signature> signatureList;
	private MailAccount mailAccount;
	private Signature signature;
	private Boolean notify = false;
	private Boolean history = false;
	private String bcc;
	
	public LinkedList<MailAccount> getMailAccountList() {
		return mailAccountList;
	}
	public NotificationInfo setMailAccountList(LinkedList<MailAccount> mailAccountList) {
		this.mailAccountList = mailAccountList;
		return this;
	}
	public LinkedList<Signature> getSignatureList() {
		return signatureList;
	}
	public NotificationInfo setSignatureList(LinkedList<Signature> signatureList) {
		this.signatureList = signatureList;
		return this;
	}
	public MailAccount getMailAccount() {
		return mailAccount;
	}
	public NotificationInfo setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
		return this;
	}
	public Signature getSignature() {
		return signature;
	}
	public NotificationInfo setSignature(Signature signature) {
		this.signature = signature;
		return this;
	}
	public Boolean getNotify() {
		return notify;
	}
	public NotificationInfo setNotify(Boolean notify) {
		this.notify = notify;
		return this;
	}
	public Boolean getHistory() {
		return history;
	}
	public NotificationInfo setHistory(Boolean history) {
		this.history = history;
		return this;
	}
	public String getBcc() {
		return bcc;
	}
	public NotificationInfo setBcc(String bcc) {
		this.bcc = bcc;
		return this;
	}	
	
}
