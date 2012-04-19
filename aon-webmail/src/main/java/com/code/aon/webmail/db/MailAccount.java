package com.code.aon.webmail.db;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.enumeration.MailSource;
import com.esferalia.aon.entity.master.MailAccountDB;

@Entity
@Table(name="mail_account")
public class MailAccount extends MailAccountDB implements IMailAccount {

	private static final long serialVersionUID = 1L;
	
	public MailAccount() {
	    setProtocol("imap");
	    setIncomingPort(143);
	    setIncomingSsl(false);
	    setOutgoingVerification(true);
	    setOutgoingPort(25);
	    setOutgoingSsl(false);
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
	
	@Transient
	public boolean isDefault() {
		return StringUtils.equalsIgnoreCase(DEFAULT_MAIL_ACCOUNT_NAME, getName());
	}	

	@Override
	@Transient
	public boolean isEnterpriseAccount() {
		return (getUser() == null) || (getUser().getId() == null);
	}
}
