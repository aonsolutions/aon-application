package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;

public class AccountEntryModuleOptions extends  ModuleOptions<AccountEntryModuleOptions> {

	private static final long serialVersionUID = 8565229219550096670L;

	private Integer accountEntryId;
	private AccountingInvoice accountingInvoice;
	private TediResult tediResult;
	private ModuleCallback externalCallback;

	
	private boolean embedded = false;
	private boolean backButtonVisible = true;
	private boolean errorLogTabVisible = true;
	private boolean sessionLogTabVisible = true;
	private boolean previewSectionVisible = true;
	private boolean balancesSectionVisible = true;
	private boolean statementTabVisible = true;
	private boolean journalTabVisible = true;
	private boolean extraInfoTabVisible = true;

	public Integer getAccountEntryId() {
		return accountEntryId;
	}

	public AccountEntryModuleOptions setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
		return this;
	}

	public AccountingInvoice getAccountingInvoice() {
		return accountingInvoice;
	}

	public AccountEntryModuleOptions setAccountingInvoice(AccountingInvoice ai) {
		this.accountingInvoice = ai;
		return this;
	}

	public TediResult getTediResult() {
		return tediResult;
	}
	public AccountEntryModuleOptions setTediResult(TediResult tediResult) {
		this.tediResult = tediResult;
		return this;
	}
	
	public ModuleCallback getExternalCallback() {
		return externalCallback;
	}

	public boolean hasExternalCallback() {
		return getExternalCallback() != null;
	}

	public AccountEntryModuleOptions setExternalCallback(ModuleCallback externalCallback) {
		this.externalCallback = externalCallback;
		return this;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	public AccountEntryModuleOptions setEmbedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}
	
	public boolean isBackButtonVisible() {
		return backButtonVisible;
	}
	public AccountEntryModuleOptions setBackButtonVisible(boolean backButtonVisible) {
		this.backButtonVisible = backButtonVisible;
		return this;
	}

	public boolean isErrorLogTabVisible() {
		return errorLogTabVisible;
	}

	public AccountEntryModuleOptions setErrorLogTabVisible(boolean errorLogTabVisible) {
		this.errorLogTabVisible = errorLogTabVisible;
		return this;
	}

	public boolean isSessionLogTabVisible() {
		return sessionLogTabVisible;
	}

	public AccountEntryModuleOptions setSessionLogTabVisible(boolean sessionLogTabVisible) {
		this.sessionLogTabVisible = sessionLogTabVisible;
		return this;
	}

	public boolean isBalancesTabVisible() {
		return isPreviewSectionVisible() || isBalancesSectionVisible();
	}

	public boolean isPreviewSectionVisible() {
		return previewSectionVisible;
	}

	public AccountEntryModuleOptions setPreviewSectionVisible(boolean previewSectionVisible) {
		this.previewSectionVisible = previewSectionVisible;
		return this;
	}

	public boolean isBalancesSectionVisible() {
		return balancesSectionVisible;
	}

	public AccountEntryModuleOptions setBalancesSectionVisible(boolean balancesSectionVisible) {
		this.balancesSectionVisible = balancesSectionVisible;
		return this;
	}

	public boolean isStatementTabVisible() {
		return statementTabVisible;
	}

	public AccountEntryModuleOptions setStatementTabVisible(boolean statementTabVisible) {
		this.statementTabVisible = statementTabVisible;
		return this;
	}

	public boolean isJournalTabVisible() {
		return journalTabVisible;
	}

	public AccountEntryModuleOptions setJournalTabVisible(boolean journalTabVisible) {
		this.journalTabVisible = journalTabVisible;
		return this;
	}

	public boolean isExtraInfoTabVisible() {
		return extraInfoTabVisible;
	}

	public AccountEntryModuleOptions setExtraInfoTabVisible(boolean extraInfoTabVisible) {
		this.extraInfoTabVisible = extraInfoTabVisible;
		return this;
	}
}
