package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.Date;
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
	private Integer mode;
	private Integer logoPercentage;
	private Boolean isLogo = false;
	
	// Notice
	private Integer noticeId;
	private String body;
	private String title;
	private Date createDate;
	private Date date;
	private String userName;
	private NotificationType notificationType;
	private String companyName;
	
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
	public Integer getNoticeId() {
		return noticeId;
	}
	public NotificationInfo setNoticeId(Integer id) {
		this.noticeId = id;
		return this;
	}
	public String getBody() {
		return body;
	}
	public NotificationInfo setBody(String body) {
		this.body = body;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public NotificationInfo setTitle(String title) {
		this.title = title;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public NotificationInfo setDate(Date date) {
		this.date = date;
		return this;
	}
	public String getUserName() {
		return userName;
	}
	public NotificationInfo setUserName(String userName) {
		this.userName = userName;
		return this;
	}
	public NotificationType getNotificationType() {
		return notificationType;
	}
	public NotificationInfo setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
		return this;
	}
	public String getCompanyName() {
		return companyName;
	}
	public NotificationInfo setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
	public Integer getMode() {
		return mode;
	}
	public NotificationInfo setMode(Integer mode) {
		this.mode = mode;
		return this;
	}
	public Integer getLogoPercentage() {
		return logoPercentage;
	}
	public NotificationInfo setLogoPercentage(Integer logoPercentage) {
		this.logoPercentage = logoPercentage;
		return this;
	}
	public Boolean getIsLogo() {
		return isLogo;
	}
	public NotificationInfo setIsLogo(Boolean isLogo) {
		this.isLogo = isLogo;
		return this;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public NotificationInfo setCreateDate(Date createDate) {
		this.createDate = createDate;
		return this;
	}
	
	
	
}
