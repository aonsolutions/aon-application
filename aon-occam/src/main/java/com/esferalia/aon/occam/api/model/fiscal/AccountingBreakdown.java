package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingBreakdown implements Serializable {

	private static final long serialVersionUID = 2639661458479524314L;

	private Integer entryId;
	private Integer journal;
	private Date issueDate;
	private Integer activity;
	private String activityDescription;
	private String epigraphSection;
	private String epigraph;
	private IRPFRegime regime;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private String concept;
	private double debit;
	private double credit;

	public Integer getEntryId() {
		return entryId;
	}
	public AccountingBreakdown setEntryId(Integer entryId) {
		this.entryId = entryId;
		return this;
	}
	public Integer getJournal() {
		return journal;
	}
	public AccountingBreakdown setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}

	public AccountingBreakdown setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public Integer getActivity() {
		return activity;
	}

	public AccountingBreakdown setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public AccountingBreakdown setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}

	public AccountingBreakdown setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	
	public String getEpigraphSection() {
		return epigraphSection;
	}

	public AccountingBreakdown setEpigraphSection(String epigraphSection) {
		this.epigraphSection = epigraphSection;
		return this;
	}

	public IRPFRegime getRegime() {
		return regime;
	}

	public AccountingBreakdown setRegime(IRPFRegime regime) {
		this.regime = regime;
		return this;
	}

	public Integer getAccount() {
		return account;
	}

	public AccountingBreakdown setAccount(Integer account) {
		this.account = account;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public AccountingBreakdown setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public AccountingBreakdown setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public AccountingBreakdown setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public double getDebit() {
		return debit;
	}

	public AccountingBreakdown setDebit(double debit) {
		this.debit = debit;
		return this;
	}

	public double getCredit() {
		return credit;
	}

	public AccountingBreakdown setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	
	public double getCreditBalance() {
		return AonMathUtils.round(credit - debit);
	}
	public double getDebitBalance() {
		return AonMathUtils.round(debit - credit);
	}
	
	public boolean hasActivity() {
		return (getActivity() != null);
	}
	
	public boolean isFarmer() {
		return (hasActivity() 
			&& (AonStringUtils.isNotBlank(getEpigraphSection())
			&& AonStringUtils.isNotBlank(getEpigraph())
			&& AonStringUtils.equals(getEpigraphSection(), "1")
			&& AonStringUtils.startsWith(getEpigraph(), "0")));
	}
	public boolean isSimplifiedRegime() {
		return (hasActivity() && getRegime() == IRPFRegime.SIMPLIFIED); 
	}
	public boolean isNormalRegime() {
		return (hasActivity() && getRegime() == IRPFRegime.NORMAL); 
	}
	public boolean isObjectiveRegime() {
		return (hasActivity() && getRegime() == IRPFRegime.OBJECTIVE); 
	}
}
