package com.esferalia.aon.occam.api.model;

import java.util.Date;
import java.util.HashSet;

import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountingReportParams implements IAccountParams, Cloneable {

	private static final long serialVersionUID = -599956549562585563L;
	
	public static String EMPTY_COST_CENTER_ACCOUNT = "CUENTAS SIN CENTRO DE COSTO";
	
	private String domainName;
	private int domain;
	private String user;
	private Integer period;
	private Date fromDate;
	private Date toDate;
	private Account account;
	private int level;
	private Integer activity;
	private SecurityLevel securityLevel;
	private String documentNumber;
	private HashSet<String> costCenters;
	
	private int previousPeriods;
	
	private boolean lowLevelAccountVisible;
	private boolean noActivityAccountVisible;
	private boolean percentsEnabled;
	private boolean byMonth;
	
	private boolean openingEntriesExcluded;
	private boolean operatingEntriesExcluded;
	private boolean closingEntriesExcluded;
	
	private boolean reverseOrder;
	private BalanceType balanceType;
	
	public String getDomainName() {
		return domainName;
	}
	public AccountingReportParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	@Override
	public int getDomain() {
		return domain;
	}
	public AccountingReportParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getUser() {
		return user;
	}
	public AccountingReportParams setUser(String user) {
		this.user = user;
		return this;
	}
	@Override
	public Integer getPeriod() {
		return period;
	}
	public AccountingReportParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	@Override
	public Date getFromDate() {
		return fromDate;
	}
	public AccountingReportParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	@Override
	public Date getToDate() {
		return toDate;
	}
	public AccountingReportParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Account getAccount() {
		return account;
	}
	public AccountingReportParams setAccount(Account account) {
		this.account = account;
		return this;
	}
	
	@Override
	public Integer getActivity() {
		return activity;
	}
	public AccountingReportParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountingReportParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public int getLevel() {
		return level;
	}
	public AccountingReportParams setLevel(int level) {
		this.level = level;
		return this;
	}
	
	public boolean isLowLevelAccountVisible() {
		return lowLevelAccountVisible;
	}
	public AccountingReportParams setLowLevelAccountVisible(boolean lowLevelAccountVisible) {
		this.lowLevelAccountVisible = lowLevelAccountVisible;
		return this;
	}
	
	public boolean isNoActivityAccountVisible() {
		return noActivityAccountVisible;
	}
	public AccountingReportParams setNoActivityAccountVisible(boolean noActivityAccountVisible) {
		this.noActivityAccountVisible = noActivityAccountVisible;
		return this;
	}
	public boolean isReverseOrder() {
		return reverseOrder;
	}
	public AccountingReportParams setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
		return this;
	}
	public BalanceType getBalanceType() {
		return balanceType;
	}
	public AccountingReportParams setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
		return this;
	}
	public boolean isNormal() {
		return getBalanceType() == BalanceType.BALANCE_NORMAL;
	}
	public boolean isAbbreviate(){
		return getBalanceType() == BalanceType.BALANCE_ABBREVIATE;
	}
	public boolean isPymes(){
		return getBalanceType() == BalanceType.BALANCE_PYMES;
	}
	
	public AccountingReportParams clone() {
		return new AccountingReportParams()
			.setDomain(getDomain())
			.setPeriod(getPeriod())
			.setFromDate(getFromDate())
			.setToDate(getToDate())
			.setAccount(getAccount()==null?null:getAccount().clone())
			.setLevel(getLevel())
			.setActivity(getActivity())
			.setSecurityLevel(getSecurityLevel())
			.setLowLevelAccountVisible(isLowLevelAccountVisible())
			.setNoActivityAccountVisible(isNoActivityAccountVisible())
			.setDocumentNumber(getDocumentNumber())
			.setPreviousPeriods(getPreviousPeriods())
			.setPercentsEnabled(isPercentsEnabled())
			.setByMonth(isByMonth())
			.addAllCostCenter(getCostCenters())
			.setOperatingEntriesExcluded(areOpeningEntriesExcluded())
			.setOperatingEntriesExcluded(areOperatingEntriesExcluded())
			.setClosingEntriesExcluded(areClosingEntriesExcluded())
			.setReverseOrder(isReverseOrder())
			.setBalanceType(getBalanceType())
		;
	}
	
	// ***********************************************
	// ***********************************************
	// ***********************************************
	// ***********************************************
	public boolean areOpeningEntriesExcluded() {
		return openingEntriesExcluded;
	}

	public AccountingReportParams setOpeningEntriesExcluded(boolean openingEntriesExcluded) {
		this.openingEntriesExcluded = openingEntriesExcluded;
		return this;
	}

	public boolean areClosingEntriesExcluded() {
		return closingEntriesExcluded;
	}

	public AccountingReportParams setClosingEntriesExcluded(boolean closingEntriesExcluded) {
		this.closingEntriesExcluded = closingEntriesExcluded;
		return this;
	}

	public boolean areOperatingEntriesExcluded() {
		return operatingEntriesExcluded;
	}

	public AccountingReportParams setOperatingEntriesExcluded(boolean operatingEntriesExcluded) {
		this.operatingEntriesExcluded = operatingEntriesExcluded;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public AccountingReportParams setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	// ***********************************************
	// ***********************************************
	// ***********************************************

	public int getPreviousPeriods() {
		return previousPeriods;
	}
	public AccountingReportParams setPreviousPeriods(int previousPeriods) {
		this.previousPeriods = previousPeriods;
		return this;
	}
	
	public boolean showRatios() {
		return (getPreviousPeriods() == 0);
	}
	
	public boolean showIncreasePercent() {
		return (getPreviousPeriods() > 0);
	}

	public boolean isByMonth() {
		return byMonth;
	}
	public AccountingReportParams setByMonth(boolean byMonth) {
		this.byMonth = byMonth;
		return this;
	}
	
	public boolean isPercentsEnabled() {
		return percentsEnabled;
	}
	public AccountingReportParams setPercentsEnabled(boolean percentsEnabled) {
		this.percentsEnabled = percentsEnabled;
		return this;
	}
	public HashSet<String> getCostCenters() {
		return costCenters;
	}
	public AccountingReportParams setCostCenters(HashSet<String> costCenters) {
		this.costCenters = costCenters;
		return this;
	}
	public AccountingReportParams addCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (!costCenters.contains(costCenter)) {
			costCenters.add(costCenter);
		}
		return this;
	}
	public AccountingReportParams addAllCostCenter(HashSet<String> newCostCenters) {
		if (newCostCenters != null) {
			if (costCenters == null) setCostCenters(new HashSet<String>());
			for (String cc : newCostCenters) {
				if (!costCenters.contains(cc)) costCenters.add(cc);
			}
		}
		return this;
	}
	
	public AccountingReportParams removeCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (costCenters.contains(costCenter)) {
			costCenters.remove(costCenter);
		}
		return this;
	}
	// ***********************************************
	// ***********************************************
	// ***********************************************
	
}
