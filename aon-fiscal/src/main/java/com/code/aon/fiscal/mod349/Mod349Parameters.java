package com.code.aon.fiscal.mod349;

import java.io.Serializable;

import com.code.aon.common.AonVersion;
import com.code.aon.fiscal.Mod349;

public class Mod349Parameters implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Mod349 mod349;
	private boolean taxDateEnabled;
	private boolean groupedByNIF;
	private String domainName;
	
	public Mod349Parameters(String domainName) {
		this.domainName = domainName;		
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public void initialize() {
		setTaxDateEnabled(false);
		setGroupedByNIF(false);
	}
	
	public Mod349 getMod349() {
		return mod349;
	}
	public void setMod349(Mod349 mod349) {
		this.mod349 = mod349;
	}
	public boolean isTaxDateEnabled() {
		return taxDateEnabled;
	}
	public void setTaxDateEnabled(boolean taxDateEnabled) {
		this.taxDateEnabled = taxDateEnabled;
	}
	public boolean isGroupedByNIF() {
		return groupedByNIF;
	}
	public void setGroupedByNIF(boolean gropupedByNIF) {
		this.groupedByNIF = gropupedByNIF;
	}
}
