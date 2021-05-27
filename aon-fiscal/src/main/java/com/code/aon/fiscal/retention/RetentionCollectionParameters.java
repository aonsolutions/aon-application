package com.code.aon.fiscal.retention;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.WithholdingType;

public class RetentionCollectionParameters implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean toCustomer;
	private boolean byPercent;
	private boolean taxDateEnabled;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private String fromSeries;
	private String toSeries;
	private Integer fromNumber;
	private Integer toNumber;
	private WithholdingType withholdingType;
	private Double percent;
	private SecurityLevel securityLevel;
	private Date date;
	private String domainName;
	
	
	public RetentionCollectionParameters(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isToCustomer() {
		return toCustomer;
	}
	public void setToCustomer(boolean toCustomer) {
		this.toCustomer = toCustomer;
	}
	
	public boolean isByPercent() {
		return byPercent;
	}
	public void setByPercent(boolean byPercent) {
		this.byPercent = byPercent;
	}
	
	public boolean isTaxDateEnabled() {
		return taxDateEnabled;
	}

	public void setTaxDateEnabled(boolean taxDateEnabled) {
		this.taxDateEnabled = taxDateEnabled;
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}

	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}

	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	public String getFromSeries() {
		return fromSeries;
	}
	public void setFromSeries(String fromSeries) {
		this.fromSeries = fromSeries;
	}

	public String getToSeries() {
		return toSeries;
	}
	public void setToSeries(String toSeries) {
		this.toSeries = toSeries;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}
	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
}
