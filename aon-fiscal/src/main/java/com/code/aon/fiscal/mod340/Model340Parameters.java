package com.code.aon.fiscal.mod340;


import java.io.Serializable;
import java.util.Date;

import com.code.aon.common.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Period;

public class Model340Parameters implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer year;
	private Period period;
	private Date date;
	private Date fromDate;
	private Date toDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private SecurityLevel securityLevel;
	private boolean investmentBookEnabled;
	private boolean taxDateEnabled;
	private boolean replacement;
	private String previousNumber;
	private String vatDeclarationNumber;
	private String domain;
	private Administration administration;
	

	public Model340Parameters() {
		setDate(new Date());
		setFromDate(null);
		setToDate(null);
		setTaxDateEnabled(false);
		administration = Administration.COMMON_TERRITORY;
	}
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
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

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isTaxDateEnabled() {
		return taxDateEnabled;
	}
	public void setTaxDateEnabled(boolean taxDateEnabled) {
		this.taxDateEnabled = taxDateEnabled;
	}
	public boolean isInvestmentBookEnabled() {
		return investmentBookEnabled;
	}
	public void setInvestmentBookEnabled(boolean investmentBookEnabled) {
		this.investmentBookEnabled = investmentBookEnabled;
	}

	public String getVatDeclarationNumber() {
		return vatDeclarationNumber;
	}
	public void setVatDeclarationNumber(String vatDeclarationNumber) {
		this.vatDeclarationNumber = vatDeclarationNumber;
	}

	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}

	public String getPreviousNumber() {
		return previousNumber;
	}
	public void setPreviousNumber(String previousNumber) {
		this.previousNumber = previousNumber;
	}

	public String getPeriodString() {
		if (getPeriod() == Period.M01) return "01";
		else if (getPeriod() == Period.M02) return "02";
		else if (getPeriod() == Period.M03) return "03";
		else if (getPeriod() == Period.M04) return "04";
		else if (getPeriod() == Period.M05) return "05";
		else if (getPeriod() == Period.M06) return "06";
		else if (getPeriod() == Period.M07) return "07";
		else if (getPeriod() == Period.M08) return "08";
		else if (getPeriod() == Period.M09) return "09";
		else if (getPeriod() == Period.M10) return "10";
		else if (getPeriod() == Period.M11) return "11";
		else if (getPeriod() == Period.M12) return "12";
		else if (getPeriod() == Period.T1) return "1T";
		else if (getPeriod() == Period.T2) return "2T";
		else if (getPeriod() == Period.T3) return "3T";
		else if (getPeriod() == Period.T4) return "4T";
		return "";
	}


}
