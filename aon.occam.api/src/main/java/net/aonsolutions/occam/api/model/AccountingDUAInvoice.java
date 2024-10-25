package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class AccountingDUAInvoice implements Serializable {

	private static final long serialVersionUID = -9022998678080591926L;

	private AccountingDUAInfo info;
	private AccountingInvoice accountingInvoice;

	public AccountingDUAInfo getInfo() {
		return info;
	}

	public AccountingDUAInvoice setInfo(AccountingDUAInfo info) {
		this.info = info;
		return this;
	}

	public AccountingInvoice getAccountingInvoice() {
		return accountingInvoice;
	}

	public AccountingDUAInvoice setAccountingInvoice(AccountingInvoice accountingInvoice) {
		this.accountingInvoice = accountingInvoice;
		return this;
	}

}
