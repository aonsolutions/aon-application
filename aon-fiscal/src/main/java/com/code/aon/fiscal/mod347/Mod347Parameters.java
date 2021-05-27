package com.code.aon.fiscal.mod347;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.Mod347;

public class Mod347Parameters implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Mod347 mod347;
	private boolean taxDateEnabled;

	private boolean excludeExports;
	private boolean excludeIntracommunitaryDeliveries;
	private boolean excludeOutputExtracommunitaryServices;
	private boolean excludeOutputIntracommunitaryServices;
	private boolean excludeOutputNationalZero;
	private boolean excludeImports;
	private boolean excludeIntracommunitaryAdquisitions;
	private boolean excludeInputExtracommunitaryServices;
	private boolean excludeInputIntracommunitaryServices;
	private boolean excludeInputNationalZero;
	private boolean excludeMod180Declared;
	private boolean excludeMod190Declared;
	private boolean groupedByNIF;
	private boolean pendingAccrualPayment;
	private String domainName;
	
	public Mod347Parameters(String domainName) {
		this.domainName = domainName;
	}
	
	public void initialize() {
		setTaxDateEnabled(false);
		setExcludeExports(true);
		setExcludeIntracommunitaryDeliveries(true);
		setExcludeOutputExtracommunitaryServices(true);
		setExcludeOutputIntracommunitaryServices(true);
		setExcludeOutputNationalZero(false);
		setExcludeImports(true);
		setExcludeIntracommunitaryAdquisitions(true);
		setExcludeInputExtracommunitaryServices(false);
		setExcludeInputIntracommunitaryServices(false);
		setExcludeInputNationalZero(true);
		setExcludeMod180Declared(true);
		setExcludeMod190Declared(true);
		setGroupedByNIF(true);
		setPendingAccrualPayment(false);
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Mod347 getMod347() {
		return mod347;
	}
	public void setMod347(Mod347 mod347) {
		this.mod347 = mod347;
	}
	public boolean isTaxDateEnabled() {
		return taxDateEnabled;
	}
	public void setTaxDateEnabled(boolean taxDateEnabled) {
		this.taxDateEnabled = taxDateEnabled;
	}
	public boolean isExcludeExports() {
		return excludeExports;
	}
	public void setExcludeExports(boolean excludeExports) {
		this.excludeExports = excludeExports;
	}
	public boolean isExcludeIntracommunitaryDeliveries() {
		return excludeIntracommunitaryDeliveries;
	}
	public void setExcludeIntracommunitaryDeliveries(boolean excludeIntracommunitaryDeliveries) {
		this.excludeIntracommunitaryDeliveries = excludeIntracommunitaryDeliveries;
	}
	public boolean isExcludeOutputExtracommunitaryServices() {
		return excludeOutputExtracommunitaryServices;
	}
	public void setExcludeOutputExtracommunitaryServices(boolean excludeOutputExtracommunitaryServices) {
		this.excludeOutputExtracommunitaryServices = excludeOutputExtracommunitaryServices;
	}
	public boolean isExcludeOutputIntracommunitaryServices() {
		return excludeOutputIntracommunitaryServices;
	}
	public void setExcludeOutputIntracommunitaryServices(boolean excludeOutputIntracommunitaryServices) {
		this.excludeOutputIntracommunitaryServices = excludeOutputIntracommunitaryServices;
	}
	public boolean isExcludeOutputNationalZero() {
		return excludeOutputNationalZero;
	}
	public void setExcludeOutputNationalZero(boolean excludeOutputNationalZero) {
		this.excludeOutputNationalZero = excludeOutputNationalZero;
	}
	public boolean isExcludeImports() {
		return excludeImports;
	}
	public void setExcludeImports(boolean excludeImports) {
		this.excludeImports = excludeImports;
	}
	public boolean isExcludeIntracommunitaryAdquisitions() {
		return excludeIntracommunitaryAdquisitions;
	}
	public void setExcludeIntracommunitaryAdquisitions(boolean excludeIntracommunitaryAdquisitions) {
		this.excludeIntracommunitaryAdquisitions = excludeIntracommunitaryAdquisitions;
	}
	public boolean isExcludeInputExtracommunitaryServices() {
		return excludeInputExtracommunitaryServices;
	}
	public void setExcludeInputExtracommunitaryServices(boolean excludeInputExtracommunitaryServices) {
		this.excludeInputExtracommunitaryServices = excludeInputExtracommunitaryServices;
	}
	public boolean isExcludeInputIntracommunitaryServices() {
		return excludeInputIntracommunitaryServices;
	}
	public void setExcludeInputIntracommunitaryServices(boolean excludeInputIntracommunitaryServices) {
		this.excludeInputIntracommunitaryServices = excludeInputIntracommunitaryServices;
	}
	public boolean isExcludeInputNationalZero() {
		return excludeInputNationalZero;
	}
	public void setExcludeInputNationalZero(boolean excludeInputNationalZero) {
		this.excludeInputNationalZero = excludeInputNationalZero;
	}
	public boolean isGroupedByNIF() {
		return groupedByNIF;
	}
	public void setGroupedByNIF(boolean gropupedByNIF) {
		this.groupedByNIF = gropupedByNIF;
	}

	public boolean isPendingAccrualPayment() {
		return pendingAccrualPayment;
	}

	public void setPendingAccrualPayment(boolean pendingAccrualPayment) {
		this.pendingAccrualPayment = pendingAccrualPayment;
	}

	public boolean isExcludeMod180Declared() {
		return excludeMod180Declared;
	}

	public void setExcludeMod180Declared(boolean excludeMod180Declared) {
		this.excludeMod180Declared = excludeMod180Declared;
	}

	public boolean isExcludeMod190Declared() {
		return excludeMod190Declared;
	}

	public void setExcludeMod190Declared(boolean excludeMod190Declared) {
		this.excludeMod190Declared = excludeMod190Declared;
	}
}
