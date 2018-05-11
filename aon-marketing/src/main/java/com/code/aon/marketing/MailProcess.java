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
	
	private Integer principal;

	public MailProcessType getType() {
		return type;
	}

	public MailProcess setType(MailProcessType type) {
		this.type = type;
		return this;
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public MailProcess setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public MailProcess setTemplate(Template template) {
		this.template = template;
		return this;
	}
	
	public Integer getPrincipal() {
		return principal;
	}

	public MailProcess setPrincipal(Integer principal) {
		this.principal = principal;
		return this;
	}
	
}
