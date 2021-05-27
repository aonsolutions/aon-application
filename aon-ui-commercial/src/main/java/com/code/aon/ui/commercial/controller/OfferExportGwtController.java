package com.code.aon.ui.commercial.controller;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class OfferExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- OFFER FILTER
	private OfferStatus[] statuses;
	private String serie;
	
	private Integer target;
	private Integer seller;
	private Integer supplier;
	private Integer project;
	
	private Date fromDate;
	private Date toDate;
	private String fromDateStr; 
	private String toDateStr;
	
	private Integer fromNumber;
	private Integer toNumber;
	
	private Integer type;
	private Integer workplace;
	private Integer scope;
	
	private Boolean signed;
	private Boolean confidential;
	
	private String  statusesStr;
	
//	public static OfferExportGwtController getInstance() { 
//		return new OfferExportGwtController();
//	}
	
	public String getInitialize(){
		return "";
	}

	public OfferExportGwtController() {
	}

	public OfferStatus[] getStatuses() {
		return statuses;
	}
	public void setStatuses(com.code.aon.commercial.enumeration.OfferStatus[] statuses) {
		
		this.statuses = new OfferStatus[statuses.length];
		String s = "";
		for (Integer i=0; i<statuses.length; i++){
			s = s+"-"+statuses[i].ordinal();
			this.statuses[i] = OfferStatus.values()[statuses[i].ordinal()];
		}
		this.statusesStr= s;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Integer getSeller() {
		return seller;
	}

	public void setSeller(Integer seller) {
		this.seller = seller;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		setFromDateStr(AonDateUtils.simpleFormat(fromDate));
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		setToDateStr(AonDateUtils.simpleFormat(toDate));
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public Boolean getSigned() {
		return signed;
	}

	public void setSigned(Boolean signed) {
		this.signed = signed;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public String getStatusesStr() {
		return statusesStr;
	}

	public void setStatusesStr(String statusesStr) {
		this.statusesStr = statusesStr;
	}

	public Integer getProject() {
		return project;
	}

	public void setProject(Integer project) {
		this.project = project;
	}

	public Integer getSupplier() {
		return supplier;
	}

	public void setSupplier(Integer supplier) {
		this.supplier = supplier;
	}

	public String getFromDateStr() {
		return fromDateStr;
	}

	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}

	public String getToDateStr() {
		return toDateStr;
	}

	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}
	
}