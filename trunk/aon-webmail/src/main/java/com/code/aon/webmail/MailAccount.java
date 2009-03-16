package com.code.aon.webmail;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@Entity
@Table(name="mail_account")
@EntryObject(mainObjectClass="aonMailAccount", objectClasses={"top"})
public class MailAccount implements ITransferObject{
	
	public static final String DEFAULT_MAIL_ACCOUNT_NAME = "default";

	private static final long serialVersionUID = 3319001240608653621L;

	// ident
	private Name id;
	
	// name;
	private String name;

	// email
    private String email;

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
    private byte[] password;
    
    // Signature
    private Signature signature;
    
    private String draftFolder;
    
    private String sentFolder;
    
    private String spamFolder;
    
    private String trashFolder;
    
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@RDN
	@Attribute(name="cn", length=32768, nullable=false)    
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
    @Attribute(name="mail", length=256, nullable=false)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the host
	 */
	@Attribute(name="host", length=256)
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
	public Name getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Name id) {
		this.id = id;
	}

	/**
	 * @return the incomingHost
	 */
	@Column(name="incoming_host")
	@Attribute(name="incomingHost", length=256)
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
	@Attribute(name="incomingPort")
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
	@Attribute(name="incomingSsl")
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
	@Column(name="mail_username")
	@Attribute(name="uid",length=256)
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
	@Column(name="outgoing_host")
	@Attribute(name="outgoingHost",length=256)
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
	@Attribute(name="outgoingPort")
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
	@Attribute(name="outgoingSsl")
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
	@Attribute(name="outgoingVerification")
	public boolean isOutgoingVerification() {
		return outgoingVerification;
	}

	/**
	 * @param outgoingVerification the outgoingVerification to set
	 */
	public void setOutgoingVerification(boolean outgoingVerification) {
		this.outgoingVerification = outgoingVerification;
	}

	@Transient
	public String getPasswordString() {
		return (password != null) ? new String( password ) : null;
	}
	
	public void setPasswordString( String value ) {
		this.password = (value != null) ? value.getBytes() : null;
	}
	
	/**
	 * @return the password
	 */
	@Attribute(name="userPassword",length=128)
	public byte[] getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(byte[] password) {
		this.password = password;
	}

	/**
	 * @return the protocol
	 */
	@Attribute(name="ipServiceProtocol",length=32768)
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Cascade(CascadeType.ALL)
	@BaseDN("ou=signatures,{parent}")
	@Attribute(name="signatureMember")
	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}
	
	@Transient
	public boolean isDefault() {
		return StringUtils.equalsIgnoreCase(DEFAULT_MAIL_ACCOUNT_NAME, getName());
	}

	@Attribute(name="draftFolder", length=64)
	public String getDraftFolder() {
		return draftFolder;
	}

	public void setDraftFolder(String draftFolder) {
		this.draftFolder = draftFolder;
	}

	@Attribute(name="sentFolder", length=64)
	public String getSentFolder() {
		return sentFolder;
	}

	public void setSentFolder(String sentFolder) {
		this.sentFolder = sentFolder;
	}

	@Attribute(name="spamFolder", length=64)
	public String getSpamFolder() {
		return spamFolder;
	}

	public void setSpamFolder(String spamFolder) {
		this.spamFolder = spamFolder;
	}

	@Attribute(name="trashFolder", length=64)
	public String getTrashFolder() {
		return trashFolder;
	}

	public void setTrashFolder(String trashFolder) {
		this.trashFolder = trashFolder;
	}
	
}
