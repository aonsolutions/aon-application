package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.user.client.ui.HasWidgets;

public class AccountEntryModuleOptions implements Serializable {

	private static final long serialVersionUID = 8565229219550096670L;

	private String domainName;
	private int domain;
	private String user;

	private Integer accountEntryId;
	private HasWidgets parentWidget;
	private AonConfiguration configuration;
	private AccountingInvoice accountingInvoice;
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

	public String getDomainName() {
		return domainName;
	}

	public AccountEntryModuleOptions setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public AccountEntryModuleOptions setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public AccountEntryModuleOptions setUser(String user) {
		this.user = user;
		return this;
	}

	public Integer getAccountEntryId() {
		return accountEntryId;
	}

	public AccountEntryModuleOptions setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
		return this;
	}

	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	public AccountEntryModuleOptions setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return this;
	}

	public AonConfiguration getConfiguration() {
		return configuration;
	}

	public AccountEntryModuleOptions setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}

	public AccountingInvoice getAccountingInvoice() {
		return accountingInvoice;
	}

	public AccountEntryModuleOptions setAccountingInvoice(AccountingInvoice ai) {
		this.accountingInvoice = ai;
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
