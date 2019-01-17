package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryParams implements IAccountParams,Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private String domainName; 
	private int domain;
	private String user;
	private Integer period;
	private Date fromDate;
	private Date toDate;
	private AccountEntryType type;
	private Integer journal;
	private Integer activity;
	private SecurityLevel securityLevel;
	// private boolean confidential; 
	
	private Integer account;
	private Double debit;
	private Double credit;
	private String concept;
	private String document;
	private Integer balancingAccount;
	
	private String comments;
	private int order;
	
	private AccountPeriod selectedPeriod;
	private EnterpriseActivity selectedActivity;
	private Account selectedAccount;
	
	// Report Metadata
	private String title;
	private String subject;
	private boolean showCover;
	private int pageOffset;
	private String pageOffsetText;
	private boolean hideFilter;
	private String headerText;
	private boolean hideDateTimeOnFooter;
	private String footerText;

	public String getDomainName() {
		return domainName;
	}
	public AccountEntryParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public AccountEntryParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public AccountEntryParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Integer getPeriod() {
		return period;
	}
	public AccountEntryParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public AccountEntryParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public AccountEntryType getType() {
		return type;
	}
	public AccountEntryParams setType(AccountEntryType type) {
		this.type = type;
		return this;
	}
	public Integer getJournal() {
		return journal;
	}
	public AccountEntryParams setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public AccountEntryParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public AccountEntryParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
	public AccountEntryParams setAccount(Integer account) {
		this.account = account;
		return this;
	}
	public Double getDebit() {
		return debit;
	}
	public AccountEntryParams setDebit(Double debit) {
		this.debit = debit;
		return this;
	}
	public Double getCredit() {
		return credit;
	}
	public AccountEntryParams setCredit(Double credit) {
		this.credit = credit;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public AccountEntryParams setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public AccountEntryParams setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public AccountEntryParams setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Integer getBalancingAccount() {
		return balancingAccount;
	}
	public AccountEntryParams setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
		return this;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountEntryParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
//	public boolean isConfidential() {
//		return confidential;
//	}
//	public AccountEntryParams setConfidential(boolean confidential) {
//		this.confidential = confidential;
//		return this;
//	}
	
	public int getOrder() {
		return order;
	}
	public AccountEntryParams setOrder(int order) {
		this.order = order;
		return this;
	}
	
	public AccountPeriod getSelectedPeriod() {
		return selectedPeriod;
	}
	public AccountEntryParams setSelectedPeriod(AccountPeriod selectedPeriod) {
		this.selectedPeriod = selectedPeriod;
		return this;
	}
	public EnterpriseActivity getSelectedActivity() {
		return selectedActivity;
	}
	public AccountEntryParams setSelectedActivity(EnterpriseActivity selectedActivity) {
		this.selectedActivity = selectedActivity;
		return this;
	}
	public Account getSelectedAccount() {
		return selectedAccount;
	}
	public AccountEntryParams setSelectedAccount(Account selectedAccount) {
		this.selectedAccount = selectedAccount;
		return this;
	}
	public boolean hasDetailProperties() {
		return (account != null 
			|| (debit != null  && debit != 0.0)
			|| (credit != null && credit != 0.0)
			|| AonStringUtils.isNotEmpty( concept ) 
			|| AonStringUtils.isNotEmpty( document  ) );
	}
	
	public String getTitle() {
		return title;
	}
	public AccountEntryParams setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getSubject() {
		return subject;
	}
	public AccountEntryParams setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public boolean isShowCover() {
		return showCover;
	}
	public AccountEntryParams setShowCover(boolean showCover) {
		this.showCover = showCover;
		return this;
	}
	public int getPageOffset() {
		return pageOffset;
	}
	public AccountEntryParams setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
		return this;
	}
	public String getPageOffsetText() {
		return pageOffsetText;
	}
	public AccountEntryParams setPageOffsetText(String pageOffsetText) {
		this.pageOffsetText = pageOffsetText;
		return this;
	}
	public boolean isHideFilter() {
		return hideFilter;
	}
	public AccountEntryParams setHideFilter(boolean hideFilter) {
		this.hideFilter = hideFilter;
		return this;
	}
	public String getHeaderText() {
		return headerText;
	}
	public AccountEntryParams setHeaderText(String headerText) {
		this.headerText = headerText;
		return this;
	}
	public boolean isHideDateTimeOnFooter() {
		return hideDateTimeOnFooter;
	}
	public AccountEntryParams setHideDateTimeOnFooter(boolean hideDateTimeOnFooter) {
		this.hideDateTimeOnFooter = hideDateTimeOnFooter;
		return this;
	}
	public String getFooterText() {
		return footerText;
	}
	public AccountEntryParams setFooterText(String footerText) {
		this.footerText = footerText;
		return this;
	}

	public ReportMetadata getReportMetadata() {
		return new ReportMetadata()
				.setTitle(getTitle())
				.setSubject(getSubject())
				.setShowCover(isShowCover())
				.setPageOffset(getPageOffset())
				.setPageOffsetText(getPageOffsetText())
				.setHideFilter(isHideFilter())
				.setHeaderText(getHeaderText())
				.setHideDateTimeOnFooter(isHideDateTimeOnFooter())
				.setFooterText(getFooterText())
				;
	}
}
