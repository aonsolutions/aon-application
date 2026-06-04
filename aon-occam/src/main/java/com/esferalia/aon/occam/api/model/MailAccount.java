package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MailAccount implements Serializable{
	
	private Integer id;
	private String name;
	private String email;
	private Integer signatureId;
	
	private String signatureStr;
	
	private Byte defaultAccount;
	private String displayName;
	private Integer domain;
	private String draftFolder;
	private String incomingHost;
	private Integer incomingPort;
	private Byte incomingSecurity;
	private String mailUsername;
	private String outgoingHost;
	private Integer outgoingPort;
	private Byte outgoingSecurity;
	private Byte outgoingVerification;
	private String password;
	private String protocol;
	private String replytoMail;
	private String sentFolder;
	private String spamFolder;
	private String trashFolder;
	private MailAccountType type;
	private Integer userId;
	
	private boolean isSESVerified;
	
	public Integer getId() {
		return id;
	}
	public MailAccount setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public MailAccount setName(String name) {
		this.name = name;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public MailAccount setEmail(String email) {
		this.email = email;
		return this;
	}
	public Integer getSignatureId() {
		return signatureId;
	}
	public MailAccount setSignatureId(Integer signatureId) {
		this.signatureId = signatureId;
		return this;
	}
	
	public String getSignatureStr() {
		return signatureStr;
	}
	public MailAccount setSignatureStr(String signatureStr) {
		this.signatureStr = signatureStr;
		return this;
	}
	
	public Byte getDefaultAccount() {
		return defaultAccount;
	}
	public MailAccount setDefaultAccount(Byte defaultAccount) {
		this.defaultAccount = defaultAccount;
		return this;
	}
	public String getDisplayName() {
		return displayName;
	}
	public MailAccount setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public MailAccount setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getDraftFolder() {
		return draftFolder;
	}
	public MailAccount setDraftFolder(String draftFolder) {
		this.draftFolder = draftFolder;
		return this;
	}
	public String getIncomingHost() {
		return incomingHost;
	}
	public MailAccount setIncomingHost(String incomingHost) {
		this.incomingHost = incomingHost;
		return this;
	}
	public Integer getIncomingPort() {
		return incomingPort;
	}
	public MailAccount setIncomingPort(Integer incomingPort) {
		this.incomingPort = incomingPort;
		return this;
	}
	public Byte getIncomingSecurity() {
		return incomingSecurity;
	}
	public MailAccount setIncomingSecurity(Byte incomingSecurity) {
		this.incomingSecurity = incomingSecurity;
		return this;
	}
	public String getMailUsername() {
		return mailUsername;
	}
	public MailAccount setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
		return this;
	}
	public String getOutgoingHost() {
		return outgoingHost;
	}
	public MailAccount setOutgoingHost(String outgoingHost) {
		this.outgoingHost = outgoingHost;
		return this;
	}
	public Integer getOutgoingPort() {
		return outgoingPort;
	}
	public MailAccount setOutgoingPort(Integer outgoingPort) {
		this.outgoingPort = outgoingPort;
		return this;
	}
	public Byte getOutgoingSecurity() {
		return outgoingSecurity;
	}
	public MailAccount setOutgoingSecurity(Byte outgoingSecurity) {
		this.outgoingSecurity = outgoingSecurity;
		return this;
	}
	public Byte getOutgoingVerification() {
		return outgoingVerification;
	}
	public MailAccount setOutgoingVerification(Byte outgoingVerification) {
		this.outgoingVerification = outgoingVerification;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public MailAccount setPassword(String password) {
		this.password = password;
		return this;
	}
	public String getProtocol() {
		return protocol;
	}
	public MailAccount setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}
	public String getReplytoMail() {
		return replytoMail;
	}
	public MailAccount setReplytoMail(String replytoMail) {
		this.replytoMail = replytoMail;
		return this;
	}
	public String getSentFolder() {
		return sentFolder;
	}
	public MailAccount setSentFolder(String sentFolder) {
		this.sentFolder = sentFolder;
		return this;
	}
	public String getSpamFolder() {
		return spamFolder;
	}
	public MailAccount setSpamFolder(String spamFolder) {
		this.spamFolder = spamFolder;
		return this;
	}
	public String getTrashFolder() {
		return trashFolder;
	}
	public MailAccount setTrashFolder(String trashFolder) {
		this.trashFolder = trashFolder;
		return this;
	}
	public MailAccountType getType() {
		return type;
	}
	public MailAccount setType(MailAccountType type) {
		this.type = type;
		return this;
	}
	public Integer getUserId() {
		return userId;
	}
	public MailAccount setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}
	
	public boolean isProtocolAon() {
		return getProtocol() != null && "aon".equalsIgnoreCase(getProtocol());
	}
	
	public boolean isIncludeBcc() {
		return null != getOutgoingSecurity() && getOutgoingSecurity() == (byte) 1;
		//return getReplytoMail() != null;
	}
	
	public MailAccount setIncludeBcc(boolean includeBcc) {
		setOutgoingSecurity(includeBcc ? (byte)1 : (byte)0);
		return this;
	}
	
	public boolean isSESVerified() {
		return isSESVerified;
	}
	
	public MailAccount setSESVerified(boolean isSESVerified) {
		this.isSESVerified = isSESVerified;
		return this;
	}
	
	
}
