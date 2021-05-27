package com.code.aon.ui.project.controller;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- PROJECT FILTER

	private String name;
	private String alias;
	private String code;
	private Date from;
	private String fromStr;
	private Date to;
	private String toStr;

	public String getInitialize(){	
		this.name = "";
		this.alias = "";
		this.code = "";
		this.from = null;
		this.to = null;
		return "";
	}

	public ProjectExportGwtController() {
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
		this.fromStr = AonDateUtils.simpleFormat(from);
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
		this.toStr = AonDateUtils.simpleFormat(to);
	}
	
	public String getFromStr() {
		return fromStr;
	}

	public void setFromStr(String fromStr) {
		this.fromStr = fromStr;
	}

	public String getToStr() {
		return toStr;	
	}

	public void setToStr(String toStr) {
		this.toStr = toStr;
	}
}