package com.code.aon.webmail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.User;
import com.code.aon.webmail.enumeration.MailAccountStatus;

@Entity
@Table(name="mail_account")
public class MailAccount implements ITransferObject{

	// ident
	private Integer id;

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
    private String password;
    
	// User
    private User user;

    private MailAccountStatus status;
    
	// User
    private String blackList;

	/**
	 * @return the email
	 */
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
	@Column(name="incoming_host")
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
	@Column(name="mail_username")
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
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the User
	 */
	@ManyToOne
	@JoinColumn(name = "user", nullable = false)
	public User getUser() {
		return user;
	}

	/**
	 * @param User the User to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the status
	 */
	@Column(name="status")
	public MailAccountStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(MailAccountStatus status) {
		this.status = status;
	}

	@Column(name="black_list")
	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}

	
}
