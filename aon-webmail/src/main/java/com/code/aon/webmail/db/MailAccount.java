package com.code.aon.webmail.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.enumeration.MailSource;

@Entity
@Table(name="mail_account")
public class MailAccount implements ITransferObject, IMailAccount {

	private static final long serialVersionUID = 8209791371055358688L;
	
	public static final String MAIL_ACCOUNT_SOURCE_ID = "MailAccount.sourceId";

	public static final String MAIL_ACCOUNT_SOURCE = "MailAccount.source";
	
	public static final String MAIL_ACCOUNT_SIGNATURE_ID = "MailAccount.signature.id";

	// ident
	private Integer id;
	
	// name;
	private String name;

	// email
    private String email;

    private String replyToMail;
   
    // mail protocol imap, smtp, pop3, etc.
    private String protocol = "imap";

    // DNS host name of email server.
    private String host;

    // incoming host name
    private String incomingHost;

    // Connection incomingPort on the host
    private int incomingPort = 143;

    // account needs incomingSsl connection
    private boolean incomingSsl = false;

    // verifcaction flag for outgoing server.
    private boolean outgoingVerification = true;

    // incoming host name
    private String outgoingHost;

    // Connection incomingPort on the host
    private int outgoingPort = 25;

    // account needs incomingSsl connection
    private boolean outgoingSsl = false;

    // email user namer, or full email with mailUsername@host
    private String mailUsername;

    // UserAccount password
    private String password;
    
    // Signature
    private Signature signature;
    
    private String draftFolder;
    
    private String sentFolder;
    
    private String spamFolder;
    
    private String trashFolder;
    
    private boolean defaultAccount;
    
    private String displayName;
    
	private MailSource source = MailSource.USER;

	private Integer sourceId;

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(nullable=false,length=64)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
    
	/**
	 * @return the email
	 */
	@Column(nullable=false,length=256)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name="replyto_mail",length=256)
	public String getReplyToMail() {
		return replyToMail;
	}

	public void setReplyToMail(String replyToMail) {
		this.replyToMail = replyToMail;
	}
	
	/**
	 * @return the host
	 */
	@Column(length=256)
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the incomingHost
	 */
	@Column(name="incoming_host",length=256)
	public String getIncomingHost() {
		return incomingHost;
	}

	/**
	 * @param incomingHost the incomingHost to set
	 */
	public void setIncomingHost(String incomingHost) {
		this.incomingHost = incomingHost;
	}

	/**
	 * @return the incomingPort
	 */
	@Column(name="incoming_port")
	public int getIncomingPort() {
		return incomingPort;
	}

	/**
	 * @param incomingPort the incomingPort to set
	 */
	public void setIncomingPort(int incomingPort) {
		this.incomingPort = incomingPort;
	}

	/**
	 * @return the incomingSsl
	 */
	@Column(name="incoming_ssl")
	public boolean isIncomingSsl() {
		return incomingSsl;
	}

	/**
	 * @param incomingSsl the incomingSsl to set
	 */
	public void setIncomingSsl(boolean incomingSsl) {
		this.incomingSsl = incomingSsl;
	}

	/**
	 * @return the mailUsername
	 */
	@Column(name="mail_username",length=256)
	public String getMailUsername() {
		return mailUsername;
	}

	/**
	 * @param mailUsername the mailUsername to set
	 */
	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	/**
	 * @return the outgoingHost
	 */
	@Column(name="outgoing_host",length=256)
	public String getOutgoingHost() {
		return outgoingHost;
	}

	/**
	 * @param outgoingHost the outgoingHost to set
	 */
	public void setOutgoingHost(String outgoingHost) {
		this.outgoingHost = outgoingHost;
	}

	/**
	 * @return the outgoingPort
	 */
	@Column(name="outgoing_port")
	public int getOutgoingPort() {
		return outgoingPort;
	}

	/**
	 * @param outgoingPort the outgoingPort to set
	 */
	public void setOutgoingPort(int outgoingPort) {
		this.outgoingPort = outgoingPort;
	}

	/**
	 * @return the outgoingSsl
	 */
	@Column(name="outgoing_ssl")
	public boolean isOutgoingSsl() {
		return outgoingSsl;
	}

	/**
	 * @param outgoingSsl the outgoingSsl to set
	 */
	public void setOutgoingSsl(boolean outgoingSsl) {
		this.outgoingSsl = outgoingSsl;
	}

	/**
	 * @return the outgoingVerification
	 */
	@Column(name="outgoing_verification")
	public boolean isOutgoingVerification() {
		return outgoingVerification;
	}

	/**
	 * @param outgoingVerification the outgoingVerification to set
	 */
	public void setOutgoingVerification(boolean outgoingVerification) {
		this.outgoingVerification = outgoingVerification;
	}

	/**
	 * @return the password
	 */
	@Column(name="password",length=128)
	public String getPasswordString() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPasswordString(String password) {
		this.password = password;
	}

	/**
	 * @return the protocol
	 */
	@Column(length=64)
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@ManyToOne
	@JoinColumn(name = "signature")
	@ForeignKey(name = "FK_MAIL_ACCOUNT_SIGNATURE")
	@Index(name = "IDX_MAIL_ACCOUNT_SIGNATURE")							
	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	@Override
	@Transient
	public ISignature getISignature() {
		return getSignature();
	}

	@Override
	@Transient
	public void setISignature(ISignature signature) {
		setSignature( (Signature) signature );
	}
	
	@Column(name="draft_folder",length=64)
	public String getDraftFolder() {
		return draftFolder;
	}

	public void setDraftFolder(String draftFolder) {
		this.draftFolder = draftFolder;
	}

	@Column(name="sent_folder",length=64)
	public String getSentFolder() {
		return sentFolder;
	}

	public void setSentFolder(String sentFolder) {
		this.sentFolder = sentFolder;
	}

	@Column(name="spam_folder",length=64)
	public String getSpamFolder() {
		return spamFolder;
	}

	public void setSpamFolder(String spamFolder) {
		this.spamFolder = spamFolder;
	}

	@Column(name="trash_folder",length=64)
	public String getTrashFolder() {
		return trashFolder;
	}

	public void setTrashFolder(String trashFolder) {
		this.trashFolder = trashFolder;
	}
	
	@Transient
	public boolean isDefault() {
		return StringUtils.equalsIgnoreCase(DEFAULT_MAIL_ACCOUNT_NAME, getName());
	}	

	@Column(name="default_account")
	public boolean isDefaultAccount() {
		return defaultAccount;
	}

	public void setDefaultAccount(boolean defaultAccount) {
		this.defaultAccount = defaultAccount;
	}
	
	@Column(name="display_name",length=256)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}	
	
	@Column(nullable=false)
	public MailSource getSource() {
		return source;
	}
	
	public void setSource(MailSource source) {
		this.source = source;
	}
	
	@Column(name="source_id")
	public Integer getSourceId() {
		return sourceId;
	}
	
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}		
	
	@Override
	@Transient
	public boolean isEnterpriseAccount() {
		return (this.source == MailSource.ENTERPRISE);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final MailAccount o = (MailAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.defaultAccount, o.defaultAccount)
				.append(this.displayName, o.displayName)
				.append(this.draftFolder, o.draftFolder)
				.append(this.email, o.email)				
				.append(this.host, o.host)
				.append(this.incomingHost, o.incomingHost)				
				.append(this.incomingPort, o.incomingPort)
				.append(this.incomingSsl, o.incomingSsl)
				.append(this.mailUsername, o.mailUsername)				
				.append(this.name, o.name)
				.append(this.outgoingHost, o.outgoingHost)				
				.append(this.outgoingPort, o.outgoingPort)
				.append(this.outgoingSsl, o.outgoingSsl)
				.append(this.outgoingVerification, o.outgoingVerification)				
				.append(this.password, o.password)
				.append(this.protocol, o.protocol)				
				.append(this.replyToMail, o.replyToMail)
				.append(this.sentFolder, o.sentFolder)
				.append(this.signature, o.signature)
				.append(this.source, o.source)
				.append(this.sourceId, o.sourceId)				
				.append(this.spamFolder, o.spamFolder)				
				.append(this.trashFolder, o.trashFolder)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(defaultAccount)
			.append(displayName)
			.append(draftFolder)
			.append(email)
			.append(host)
			.append(id)	
			.append(incomingHost)			
			.append(incomingPort)
			.append(incomingSsl)			
			.append(mailUsername)
			.append(name)			
			.append(outgoingHost)
			.append(outgoingPort)			
			.append(outgoingSsl)
			.append(outgoingVerification)			
			.append(password)			
			.append(protocol)
			.append(replyToMail)
			.append(sentFolder)			
			.append(signature)
			.append(source)
			.append(sourceId)		
			.append(spamFolder)
			.append(trashFolder)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
