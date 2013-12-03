package com.code.aon.webmail.db;

import static com.code.aon.webmail.bean.IMailConstants.DEFAULT_SMTP_PORT;
import static com.code.aon.webmail.bean.IMailConstants.IMAP;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.esferalia.aon.entity.master.MailAccountDB;

@Entity
@Table(name="mail_account")
public class MailAccount extends MailAccountDB implements IMailAccount {

	private static final long serialVersionUID = 1L;
	
	public MailAccount() {
	    setIncomingSecurity(ConnectionSecurity.NONE);
	    setOutgoingVerification(true);
	    setOutgoingPort(DEFAULT_SMTP_PORT);
	    setOutgoingSecurity(ConnectionSecurity.NONE);
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

	@Override
	@Transient
	public boolean isEnterpriseAccount() {
		return getUser()==null || getUser().getId() == null;
	}

	@Override
	@Transient
	public boolean isIMAP() {
		return StringUtils.equals(IMAP, getProtocol());
	}
	
}
