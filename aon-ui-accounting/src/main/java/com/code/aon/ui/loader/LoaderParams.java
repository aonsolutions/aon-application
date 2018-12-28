package com.code.aon.ui.loader;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.product.ProductCategory;

public class LoaderParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	private ProductCategory category;
	private Period accountPeriod;
	private Long bytesRead;
	private boolean documentValidable;
	private boolean forceRegistryInsert;
	private boolean ignoreExistingDomains;
	private boolean updateAccountDescription;
	private String password;
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm:ss");
	private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#,##0.00");
	static {
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
//		dfs.setGroupingSeparator('.');
//		dfs.setDecimalSeparator(',');
		NUMBER_FORMATTER.setDecimalFormatSymbols(dfs);	
	}

	public Long getBytesRead() {
		return bytesRead;
	}
	public void setBytesRead(Long bytesRead) {
		this.bytesRead = bytesRead;
	}
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	public Period getAccountPeriod() {
		return accountPeriod;
	}
	public void setAccountPeriod(Period accountPeriod) {
		this.accountPeriod = accountPeriod;
	}
	
	public DateFormat getDateFormatter() {
		return DATE_FORMATTER; 
	}
	public DateFormat getTimeFormatter() {
		return TIME_FORMATTER; 
	}
	public DecimalFormat getNumberFormatter() {
		return NUMBER_FORMATTER; 
	}
	public void addBytes(int length) {
		setBytesRead( getBytesRead() + bytesRead);
	}
	public boolean isDocumentValidable() {
		return documentValidable;
	}
	public void setDocumentValidable(boolean documentValidable) {
		this.documentValidable = documentValidable;
	}
	public boolean isForceRegistryInsert() {
		return forceRegistryInsert;
	}
	public void setForceRegistryInsert(boolean forceRegistryInsert) {
		this.forceRegistryInsert = forceRegistryInsert;
	}
	public boolean isIgnoreExistingDomains() {
		return ignoreExistingDomains;
	}
	public void setIgnoreExistingDomains(boolean ignoreExistingDomains) {
		this.ignoreExistingDomains = ignoreExistingDomains;
	}
	public boolean isUpdateAccountDescription() {
		return updateAccountDescription;
	}
	public void setUpdateAccountDescription(boolean updateAccountDescription) {
		this.updateAccountDescription = updateAccountDescription;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
