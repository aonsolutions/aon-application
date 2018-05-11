package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.type.MailProcessType;

public class MailProcess {

private MailProcessType type;
	
	private Integer id;
	private MailAccount mailAccount;
	private MailTemplate template;
	private Integer priority;

	
	public Integer getId() {
		return id;
	}
	
	public MailProcess setId(Integer id) {
		this.id = id;
		return this;
	}
	
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

	public MailTemplate getTemplate() {
		return template;
	}

	public MailProcess setTemplate(MailTemplate template) {
		this.template = template;
		return this;
	}

	public String getPriorityName() {
		return priority == 1 ? "Principal" : "Alternativo";
	}

	public Integer getPriority() {
		return priority;
	}

	public MailProcess setPriority(Integer priority) {
		this.priority = priority;
		return this;
	}
}
