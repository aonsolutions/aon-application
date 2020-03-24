package com.code.aon.ui.account.controller;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;

public class PGCExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- OFFER FILTER
	private String code;
	private String description;
	private String alias;
	private String active;
	private String costCenter;
	private String entryEnabled;
	
	public PGCExportGwtController() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getEntryEnabled() {
		return entryEnabled;
	}

	public void setEntryEnabled(String entryEnabled) {
		this.entryEnabled = entryEnabled;
	}

	public void calculate(String expression) {
		System.out.println(expression);
	}
}