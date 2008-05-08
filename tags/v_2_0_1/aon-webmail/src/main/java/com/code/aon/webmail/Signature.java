package com.code.aon.webmail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.webmail.enumeration.SignatureType;

@Entity
@Table(name="signature")
public class Signature implements ITransferObject{

	// ident
	private Integer id;

	// MailAccount
    private MailAccount mailAccount;

	// signature
    private String signature;

	// name
    private String name;

	// active
    private SignatureType active;

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
	 * @return the mailAccount
	 */
	@ManyToOne
	@JoinColumn(name = "mail_account", nullable = false)
	public MailAccount getMailAccount() {
		return mailAccount;
	}

	/**
	 * @param mailAccount the mailAccount to set
	 */
	public void setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the active
	 */
	@Column(nullable=false)
	public SignatureType getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(SignatureType active) {
		this.active = active;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    
}
