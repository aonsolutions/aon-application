package com.code.aon.marketing;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.webmail.db.MailAccount;

public class MailProcess implements ITransferObject {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private MailProcessType type;
	
	private MailAccount mailAccount;
	
	private Template template;

	public MailProcessType getType() {
		return type;
	}

	public void setType(MailProcessType type) {
		this.type = type;
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
	
}
