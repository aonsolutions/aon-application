package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingBreakdown implements Serializable {

	private static final long serialVersionUID = 2639661458479524314L;

	private Date issueDate;
	private String epigraphSection;
	private String epigraph;
	private IRPFRegime regime;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private String concept;
	private double debit;
	private double credit;

	public Date getIssueDate() {
		return issueDate;
	}

	public AccountingBreakdown setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
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
	
	public boolean isFarmer() {
		return AonStringUtils.isNotBlank(getEpigraphSection())
			&& AonStringUtils.isNotBlank(getEpigraph())
			&& AonStringUtils.equals(getEpigraphSection(), "1")
			&& AonStringUtils.startsWith(getEpigraph(), "0");
	}
	public boolean isSimplifiedRegime() {
		return  getRegime() == IRPFRegime.SIMPLIFIED; 
	}
	public boolean isNormalRegime() {
		return  getRegime() == IRPFRegime.NORMAL; 
	}
	public boolean isObjectiveRegime() {
		return  getRegime() == IRPFRegime.OBJECTIVE; 
	}
}
