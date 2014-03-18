package com.code.aon.fiscal.vat.tax;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.enumeration.Period;

public class VatTaxParameters implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String domainName;
	private int domain;
	private Date date;
	private Integer year;
	private Date fromDate;
	private Date toDate;
	private Period period;
	private VatTax	vatTax;
	private SecurityLevel securityLevel;
	private InvoiceStatus invoiceStatus;
	
	// No hay que realizar calculos, ni de facturas, ni de lo declarado anteriormente.
	private boolean mod303AvailableByDifferenceDisabled;

	public VatTaxParameters(String domainName, int domain) {
		this.domainName = domainName;
		this.domain = domain;
		setDate(new Date());
	}

	public int getDomain() {
		return domain;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public VatTax getVatTax() {
		return vatTax;
	}
	public void setVatTax(VatTax vatTax) {
		this.vatTax = vatTax;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public boolean isMod303AvailableByDifferenceDisabled() {
		return mod303AvailableByDifferenceDisabled;
	}

	public void setMod303AvailableByDifferenceDisabled(
			boolean mod303AvailableByDifferenceDisabled) {
		this.mod303AvailableByDifferenceDisabled = mod303AvailableByDifferenceDisabled;
	}
	
}
