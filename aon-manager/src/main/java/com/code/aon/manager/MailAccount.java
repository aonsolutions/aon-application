package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.MAIL_ACCOUNT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.webmail.bean.IMailConstants.IMAP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.enumeration.ConnectionSecurity;

@EntryObject(mainObjectClass=MAIL_ACCOUNT, objectClasses={TOP})
public class MailAccount implements ILdapTransferObject, IMailAccount {
	
	private static final long serialVersionUID = 3319001240608653621L;

	// ident
	private Name id;
	
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
    private byte[] password;
    
    // Signature
    private Signature signature;
    
    private String draftFolder;
    
    private String sentFolder;
    
    private String spamFolder;
    
    private String trashFolder;
    
    private boolean defaultAccount;
    
    private String displayName;
    
    private boolean enterpriseAccount;
    
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@RDN
	@Attribute(name=COMMON_NAME_ATTRIBUTE, length=32768, nullable=false)    
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
    @Attribute(name=MAIL_ATTRIBUTE, length=256, nullable=false)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Attribute(name="replyToMail")
	public String getReplyToMail() {
		return replyToMail;
	}

	public void setReplyToMail(String replyToMail) {
		this.replyToMail = replyToMail;
	}

	/**
	 * @return the host
	 */
	@Attribute(name=HOST_ATTRIBUTE, length=256)
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
	@Attribute(name=USER_ID_ATTRIBUTE,length=256)
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

	public String getPasswordString() {
		return (password != null) ? new String( password ) : null;
	}
	
	public void setPasswordString( String value ) {
		this.password = (value != null) ? value.getBytes() : null;
	}
	
	/**
	 * @return the password
	 */
	@Attribute(name=USER_PASSWORD_ATTRIBUTE,length=128)
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
	@Attribute(name=SIGNATURE_MEMBER_ATTRIBUTE)
	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}
	
	@Override
	public ISignature getISignature() {
		return getSignature();
	}

	@Override
	public void setISignature(ISignature signature) {
		setSignature( (Signature) signature );
	}

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

	@Attribute(name=DEFAULT_ACCOUNT_ATTRIBUTE)
	public boolean isDefaultAccount() {
		return defaultAccount;
	}

	public void setDefaultAccount(boolean defaultAccount) {
		this.defaultAccount = defaultAccount;
	}
	
	@Attribute(name=DISPLAY_NAME_ATTRIBUTE)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}	

	@Override
	public boolean isEnterpriseAccount() {
		return enterpriseAccount;
	}

	public void setEnterpriseAccount(boolean enterpriseAccount) {
		this.enterpriseAccount = enterpriseAccount;
	}
	
	@Override
	public ConnectionSecurity getIncomingSecurity() {
		return isIncomingSsl() ? ConnectionSecurity.SSL : ConnectionSecurity.NONE;
	}

	public void setIncomingSecurity( ConnectionSecurity cs ) {
		setIncomingSsl( cs == ConnectionSecurity.SSL );
	}	
	
	@Override
	public ConnectionSecurity getOutgoingSecurity() {
		return isOutgoingSsl() ? ConnectionSecurity.SSL : ConnectionSecurity.NONE;
	}

	public void setOutgoingSecurity( ConnectionSecurity cs) {
		setOutgoingSsl( cs == ConnectionSecurity.SSL );
	}

	@Override
	public boolean isIMAP() {
		return StringUtils.equals(IMAP, getProtocol());
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
			.append(spamFolder)
			.append(trashFolder)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
