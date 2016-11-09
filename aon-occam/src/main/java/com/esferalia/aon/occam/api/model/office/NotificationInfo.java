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
	
	private Boolean notifyOpen = false;
	private Boolean notifyClose = false;
	private Boolean notifyReopen = false;
	private Boolean notifyComment = false;
	private Boolean notifyAssignee = false;

	private Boolean commentsHistory = false;
	private Boolean statusHistory = false;
	
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
	private String state;
	
	private String description;
	
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
	public Boolean getNotifyOpen() {
		return notifyOpen;
	}
	public NotificationInfo setNotifyOpen(Boolean notifyOpen) {
		this.notifyOpen = notifyOpen;
		return this;
	}
	public Boolean getNotifyClose() {
		return notifyClose;
	}
	public NotificationInfo setNotifyClose(Boolean notifyClose) {
		this.notifyClose = notifyClose;
		return this;
	}
	public Boolean getNotifyReopen() {
		return notifyReopen;
	}
	public NotificationInfo setNotifyReopen(Boolean notifyReopen) {
		this.notifyReopen = notifyReopen;
		return this;
	}
	public Boolean getNotifyComment() {
		return notifyComment;
	}
	public NotificationInfo setNotifyComment(Boolean notifyComment) {
		this.notifyComment = notifyComment;
		return this;
	}
	public Boolean getNotifyAssignee() {
		return notifyAssignee;
	}
	public NotificationInfo setNotifyAssignee(Boolean notifyAssignee) {
		this.notifyAssignee = notifyAssignee;
		return this;
	}
	public Boolean getCommentsHistory() {
		return commentsHistory;
	}
	public NotificationInfo setCommentsHistory(Boolean commentsHistory) {
		this.commentsHistory = commentsHistory;
		return this;
	}
	public Boolean getStatusHistory() {
		return statusHistory;
	}
	public NotificationInfo setStatusHistory(Boolean statusHistory) {
		this.statusHistory = statusHistory;
		return this;
	}
	public String getState() {
		return state;
	}
	public NotificationInfo setState(String state) {
		this.state = state;
		return this;
	}
	
	public String getDescription(){
		return description;
	}
	
	public NotificationInfo setDescription(String description){
		this.description = description;
		return this;
	}
	
}
